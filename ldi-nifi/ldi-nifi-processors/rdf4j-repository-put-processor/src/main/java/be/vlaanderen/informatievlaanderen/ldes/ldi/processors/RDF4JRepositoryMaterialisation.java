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
import org.apache.nifi.processor.util.StandardValidators;
import org.eclipse.rdf4j.common.transaction.IsolationLevels;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.base.AbstractIRI;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Tags({ "ldes, rdf4j-repository, vsds" })
@CapabilityDescription("Materialises LDES events into an RDF4J repository")
@InputRequirement(InputRequirement.Requirement.INPUT_REQUIRED)
public class RDF4JRepositoryMaterialisation extends AbstractProcessor {
	private RepositoryManager repositoryManager;

	static final Relationship REL_SUCCESS = new Relationship.Builder().name("success")
			.description("A FlowFile is routed to this relationship after the database is successfully updated")
			.build();
	static final Relationship REL_FAILURE = new Relationship.Builder().name("failure").description(
			"A FlowFile is routed to this relationship if the database cannot be updated and retrying the operation will also fail, "
					+ "such as an invalid query or an integrity constraint violation")
			.build();

	static final PropertyDescriptor SPARQL_HOST = new PropertyDescriptor.Builder()
			.name("REF4J remote repository location").description("The hostname and port of the server.")
			.defaultValue("http://graphdb:7200").required(true).addValidator(StandardValidators.URL_VALIDATOR).build();

	static final PropertyDescriptor REPOSITORY_ID = new PropertyDescriptor.Builder().name("Repository ID")
			.description("The repository to connect to.").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	static final PropertyDescriptor NAMED_GRAPH = new PropertyDescriptor.Builder().name("Named graph")
			.description("If set, the named graph the triples will be written to.").required(false)
			.addValidator(StandardValidators.URI_VALIDATOR).build();

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		this.repositoryManager = new RemoteRepositoryManager(context.getProperty(SPARQL_HOST).getValue());
	}

	@Override
	public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
		// @todo Find out the ideal number of FlowFiles to process in one transaction.
		final List<FlowFile> flowFiles = session.get(50);

		Repository repository = repositoryManager.getRepository(context.getProperty(REPOSITORY_ID).getValue());
		if (flowFiles.isEmpty()) {
			return;
		}
		final AtomicBoolean committed = new AtomicBoolean(false);
		try (RepositoryConnection dbConnection = repository.getConnection()) {
			// As we are bulk-loading, set isolation level to none for improved performance.
			dbConnection.setIsolationLevel(IsolationLevels.NONE);
			dbConnection.begin();

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
						} else
							dbConnection.add(updateModel);
					}
				});
			}
			dbConnection.commit();
			committed.set(true);
		}
		for (FlowFile flowFile : flowFiles) {
			if (committed.get())
				session.transfer(flowFile, REL_SUCCESS);
			else
				session.transfer(flowFile, REL_FAILURE);
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
		Stack<Resource> subjectStack = new Stack<>();
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

	@Override
	public Set<Relationship> getRelationships() {
		final Set<Relationship> relationships = new HashSet<>();
		relationships.add(REL_SUCCESS);
		relationships.add(REL_FAILURE);
		return relationships;
	}

	@Override
	protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		final List<PropertyDescriptor> properties = new ArrayList<>();
		properties.add(SPARQL_HOST);
		properties.add(REPOSITORY_ID);
		properties.add(NAMED_GRAPH);
		return properties;
	}
}
