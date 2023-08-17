package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.ModelParseIOException;
import org.eclipse.rdf4j.common.transaction.IsolationLevels;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Materialiser {
	private final String repositoryId;
	private final String namedGraph;
	protected RepositoryManager repositoryManager;

	public Materialiser(String hostUrl, String repositoryId, String namedGraph) {
		this.repositoryId = repositoryId;
		this.namedGraph = namedGraph;
		initRepositoryManager(new RemoteRepositoryManager(hostUrl));
	}

	protected void initRepositoryManager(RepositoryManager manager) {
		this.repositoryManager = manager;
	}

	public void process(String content) {
		final Repository repository = repositoryManager.getRepository(repositoryId);

		try (RepositoryConnection dbConnection = repository.getConnection()) {
			dbConnection.setIsolationLevel(IsolationLevels.NONE);
			dbConnection.begin();

			var updateModel = readInputString(content);

			Set<Resource> entityIds = getSubjectsFromModel(updateModel);
			deleteEntitiesFromRepo(entityIds, dbConnection);

			if (namedGraph != null && !namedGraph.isEmpty()) {
				var namedGraphIRI = dbConnection.getValueFactory().createIRI(namedGraph);
				dbConnection.add(updateModel, namedGraphIRI);
			} else {
				dbConnection.add(updateModel);
			}
			dbConnection.commit();
		}
	}

	/**
	 * Returns all subjects ('real' URIs) present in the model.
	 *
	 * @param model
	 *            A graph
	 * @return A set of subject URIs.
	 */
	protected static Set<Resource> getSubjectsFromModel(Model model) {
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
	protected static void deleteEntitiesFromRepo(Set<Resource> entityIds, RepositoryConnection connection) {
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

	private Model readInputString(String content) {
		InputStream in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		try {
			return Rio.parse(in, "", RDFFormat.NQUADS);
		} catch (IOException e) {
			throw new ModelParseIOException(content, e.getMessage());
		}
	}
}
