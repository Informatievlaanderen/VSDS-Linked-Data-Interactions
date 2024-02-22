package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.MaterialisationFailedException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.JenaToRDF4JConverter;
import org.eclipse.rdf4j.common.transaction.IsolationLevels;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.base.AbstractIRI;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.CustomHTTPRepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class Materialiser {
	private final String namedGraph;
	protected RepositoryManager repositoryManager;
	private ScheduledExecutorService scheduledExecutorService;
	private final int batchSize;
	private final int batchTimeout;
	private int uncommittedMembers = 0;
	protected final RepositoryConnection dbConnection;

	public Materialiser(String hostUrl, String repositoryId, String namedGraph, int batchSize, int batchTimeout) {
		this(new RemoteRepositoryManager(hostUrl), repositoryId, namedGraph, batchSize, batchTimeout);
	}

	public Materialiser(RepositoryManager repositoryManager, String repositoryId, String namedGraph, int batchSize, int batchTimeout) {
		this.repositoryManager = repositoryManager;
		this.namedGraph = namedGraph;
		this.batchSize = batchSize;
		this.batchTimeout = batchTimeout;
		this.dbConnection = initRepositoryConnection(repositoryManager.getRepository(repositoryId));
		initExecutor();
	}

	public void process(org.apache.jena.rdf.model.Model jenaModel) {
		try {
			Model updateModel = JenaToRDF4JConverter.convert(jenaModel);

			Set<Resource> entityIds = getSubjectsFromModel(updateModel);
			deleteEntitiesFromRepo(entityIds);
			addModelToConnection(updateModel);

			if (uncommittedMembers >= batchSize) {
				commitMembers();
				resetExecutor();
			}
		} catch (Exception e) {
			dbConnection.rollback();
			throw new MaterialisationFailedException(e);
		}
	}

	public void shutdown() {
		if(dbConnection.isActive()) {
			dbConnection.commit();
		}
		dbConnection.close();
		scheduledExecutorService.shutdown();
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
	 */
	protected void deleteEntitiesFromRepo(Set<Resource> entityIds) {
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

			dbConnection.getStatements(subject, null, null).forEach((Statement statement) -> {
				Value object = statement.getObject();
				if (object.isBNode()) {
					subjectStack.push((Resource) object);
				}
			});

			dbConnection.remove(subject, null, null);
		}
	}

	private void addModelToConnection(Model updateModel) {
		if (namedGraph != null && !namedGraph.isEmpty()) {
			var namedGraphIRI = dbConnection.getValueFactory().createIRI(namedGraph);
			dbConnection.add(updateModel, namedGraphIRI);
		} else {
			dbConnection.add(updateModel);
		}
		uncommittedMembers++;
	}

	private void commitMembers() {
		dbConnection.commit();
		uncommittedMembers = 0;
		dbConnection.begin();
	}

	private void resetExecutor() {
		scheduledExecutorService.shutdownNow();
		initExecutor();
	}

	private void initExecutor() {
		scheduledExecutorService = newSingleThreadScheduledExecutor();
		scheduledExecutorService.schedule(this::commitMembers, batchTimeout, TimeUnit.MILLISECONDS);
	}

	private RepositoryConnection initRepositoryConnection(Repository repository) {
		final RepositoryConnection connection = repository instanceof HTTPRepository
				? new CustomHTTPRepositoryConnection(repository)
				: repository.getConnection();

		connection.setIsolationLevel(IsolationLevels.NONE);
		connection.begin();

		return connection;
	}
}
