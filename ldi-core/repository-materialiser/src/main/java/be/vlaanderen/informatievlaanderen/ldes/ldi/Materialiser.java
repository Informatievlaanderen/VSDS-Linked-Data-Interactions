package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.MaterialisationFailedException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.ModelParseIOException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.MaterialiserConnectionHolder;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.eclipse.rdf4j.common.transaction.IsolationLevels;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.base.AbstractIRI;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.CustomHTTPRepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class Materialiser {
	private final String repositoryId;
	private final String namedGraph;
	protected RepositoryManager repositoryManager;
	private ScheduledExecutorService scheduledExecutorService;
	private final int batchSize;
	private final int batchTimeout;
	private int uncommittedMembers = 0;
	private final MaterialiserConnectionHolder connectionHolder;
	private final RepositoryConnection dbConnection;


	public Materialiser(String hostUrl, String repositoryId, String namedGraph, int batchSize, int batchTimeout) {
		this(new RemoteRepositoryManager(hostUrl), repositoryId, namedGraph, batchSize, batchTimeout);
	}

	public Materialiser(RepositoryManager repositoryManager, String repositoryId, String namedGraph, int batchSize, int batchTimeout) {
		this.repositoryManager = repositoryManager;
		this.repositoryId = repositoryId;
		this.namedGraph = namedGraph;
		this.batchSize = batchSize;
		this.batchTimeout = batchTimeout;
		this.connectionHolder = MaterialiserConnectionHolder.initializeFromRepository(repositoryManager.getRepository(repositoryId));
		this.dbConnection = repositoryManager.getRepository(repositoryId).getConnection();
	}

	public void process(org.apache.jena.rdf.model.Model jenaModel) {
//		final RepositoryConnection dbConnection = connectionHolder.getConnection();

		try {
			dbConnection.setIsolationLevel(IsolationLevels.NONE);
			dbConnection.begin();

			var updateModel = toRdf4jModel(jenaModel);

			Set<Resource> entityIds = getSubjectsFromModel(updateModel);
			deleteEntitiesFromRepo(entityIds, dbConnection);

			if (namedGraph != null && !namedGraph.isEmpty()) {
				var namedGraphIRI = dbConnection.getValueFactory().createIRI(namedGraph);
				dbConnection.add(updateModel, namedGraphIRI);
			} else {
				dbConnection.add(updateModel);
			}

			uncommittedMembers++;

			if (uncommittedMembers >= batchSize) {
				dbConnection.commit();
				uncommittedMembers = 0;
			}

		} catch (Exception e) {
			throw new MaterialisationFailedException(e);
		}

	}

	public void shutdown() {
		dbConnection.commit();
		dbConnection.close();
		repositoryManager.shutDown();
	}

	/**
	 * Returns all subjects ('real' URIs) present in the model.
	 *
	 * @param model A graph
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
	 * @param entityIds  The subjects of the entities to delete.
	 * @param connection The DB connection.
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

	private Model toRdf4jModel(org.apache.jena.rdf.model.Model jenaModel) {
		String content = RDFWriter.source(jenaModel).lang(Lang.NQUADS).asString();
		InputStream in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		try {
			return Rio.parse(in, "", RDFFormat.NQUADS);
		} catch (IOException e) {
			throw new ModelParseIOException(content, e.getMessage());
		}
	}


	private void commitMembers() {
		dbConnection.commit();
		uncommittedMembers = 0;
		dbConnection.begin();
	}

	private void initExecutor() {
		scheduledExecutorService = newSingleThreadScheduledExecutor();
		scheduledExecutorService.schedule(this::commitMembers, batchTimeout, TimeUnit.MILLISECONDS);
	}

	private void resetExecutor() {
		scheduledExecutorService.shutdownNow();
		initExecutor();
	}

}
