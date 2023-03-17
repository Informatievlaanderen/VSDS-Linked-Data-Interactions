package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.nifi.annotation.behavior.InputRequirement;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.io.InputStreamCallback;
import org.eclipse.rdf4j.common.transaction.IsolationLevels;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.base.AbstractIRI;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RDF4JRepositoryPutMaterialisationProcessorProperties.NAMED_GRAPH;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RDF4JRepositoryPutMaterialisationProcessorProperties.REPOSITORY_ID;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RDF4JRepositoryPutMaterialisationProcessorProperties.SIMULTANEOUS_FLOWFILES_TO_PROCESS;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RDF4JRepositoryPutMaterialisationProcessorProperties.SPARQL_HOST;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({ "ldes, rdf4j-repository, vsds" })
@CapabilityDescription("Materialises LDES events into an RDF4J repository")
@InputRequirement(InputRequirement.Requirement.INPUT_REQUIRED)
public class RDF4JRepositoryMaterialisationProcessor extends AbstractProcessor {

	private RepositoryManager repositoryManager;
	private Repository repository;

	@Override
	public Set<Relationship> getRelationships() {
		final Set<Relationship> relationships = new HashSet<>();
		relationships.add(SUCCESS);
		relationships.add(FAILURE);
		return relationships;
	}

	@Override
	protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		final List<PropertyDescriptor> properties = new ArrayList<>();
		properties.add(SPARQL_HOST);
		properties.add(REPOSITORY_ID);
		properties.add(NAMED_GRAPH);
		properties.add(SIMULTANEOUS_FLOWFILES_TO_PROCESS);
		return properties;
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		this.repositoryManager = new RemoteRepositoryManager(context.getProperty(SPARQL_HOST).getValue());
		this.repository = repositoryManager.getRepository(context.getProperty(REPOSITORY_ID).getValue());
	}

	@Override
	public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
		final List<FlowFile> flowFiles = session.get(Integer.valueOf(
				context.getProperty(SIMULTANEOUS_FLOWFILES_TO_PROCESS).getValue()));

		if (flowFiles.isEmpty()) {
			return;
		}

		
		final AtomicBoolean committed = new AtomicBoolean(false);

		try (RepositoryConnection dbConnection = repository.getConnection()) {
			// As we are bulk-loading, set isolation level to none for improved performance.
			dbConnection.begin(IsolationLevels.NONE);
			
			int test = 0;

			for (FlowFile flowFile : flowFiles) {
				session.read(flowFile, new InputStreamCallback() {
					@Override
					public void process(InputStream in) throws IOException {
						Model updateModel = Rio.parse(in, "", RDFFormat.NQUADS);

						Set<Resource> entityIds = getSubjectsFromModel(updateModel);

						// Delete the old version of the entity (ldes member) from the db.
						deleteEntitiesFromRepo(entityIds, dbConnection);

						// Save the new data to the DB.
						String namedGraph = context.getProperty(NAMED_GRAPH).getValue();
						if (!namedGraph.isEmpty()) {
							IRI namedGraphIRI = dbConnection.getValueFactory().createIRI(namedGraph);
							dbConnection.add(updateModel, namedGraphIRI);
						} else {
							dbConnection.add(updateModel);
						}
					}
				});
			}

			dbConnection.commit();
			committed.set(true);
		}

		for (FlowFile flowFile : flowFiles) {
			if (committed.get()) {
				session.transfer(flowFile, SUCCESS);
			} else {
				session.transfer(flowFile, FAILURE);
			}
		}
		session.commit();
	}

	/**
	 * Returns all subjects ('real' URIs) present in the model.
	 *
	 * @param model
	 *            A graph
	 * @return A set of subject URIs.
	 */
	private static Set<Resource> getSubjectsFromModel(Model model) {
		Set<Resource> entityIds = new HashSet<>();

		model.subjects().forEach((Resource subject) -> {
			if (subject instanceof AbstractIRI) {
				entityIds.add(subject);
			}
		});

		return entityIds;
	}

	/**
	 * Delete an entity, including its blank nodes, from a repository.
	 *
	 * @param entityIds
	 *            The subjects of the entities to delete.
	 * @param connection
	 *            The DB connection.
	 */
	private static void deleteEntitiesFromRepo(Set<Resource> entityIds, RepositoryConnection connection) {
		Deque<Resource> subjectStack = new ArrayDeque<>();
		entityIds.forEach(subjectStack::push);

		/*
		 * Entities can contain blank node references. All statements with those blank
		 * node identifiers need to be removed as well. As blank nodes can be nested
		 * inside blank nodes, we need to keep track of them as they are encountered by
		 * adding them to the stack.
		 */
		while (!subjectStack.isEmpty()) {
			Resource subject = subjectStack.pop();

			connection.getStatements(subject, null, null).forEach((Statement statement) -> {
				Value object = statement.getObject();
				if (object.isBNode()) {
					subjectStack.push((Resource) object);
				}
			});

			connection.remove(subject, null, null);
		}
	}
}
