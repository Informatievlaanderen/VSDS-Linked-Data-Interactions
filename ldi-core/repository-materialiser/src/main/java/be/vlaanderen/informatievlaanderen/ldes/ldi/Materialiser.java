package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.MaterialisationFailedException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.JenaToRDF4JConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.MaterialiserConnection;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.base.AbstractIRI;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class Materialiser {
	private final MaterialiserConnection materialiserConnection;
	private ScheduledExecutorService scheduledExecutorService;
	private final int batchSize;
	private final int batchTimeout;
	private int uncommittedMembers = 0;

	public Materialiser(String hostUrl, String repositoryId, String namedGraph, int batchSize, int batchTimeout) {
		this(new RemoteRepositoryManager(hostUrl), repositoryId, namedGraph, batchSize, batchTimeout);
	}

	public Materialiser(RepositoryManager repositoryManager, String repositoryId, String namedGraph, int batchSize, int batchTimeout) {
		this.materialiserConnection = new MaterialiserConnection(repositoryManager, repositoryId, namedGraph);
		this.batchSize = batchSize;
		this.batchTimeout = batchTimeout;
		initExecutor();
	}

	protected MaterialiserConnection getMaterialiserConnection() {
		return materialiserConnection;
	}

	public void process(org.apache.jena.rdf.model.Model jenaModel) {
		try {
			Model updateModel = JenaToRDF4JConverter.convert(jenaModel);

			Set<Resource> entityIds = getSubjectsFromModel(updateModel);
			deleteEntitiesFromRepo(entityIds);
			materialiserConnection.add(updateModel);
			uncommittedMembers++;

			if (uncommittedMembers >= batchSize) {
				commitMembers();
				resetExecutor();
			}
		} catch (Exception e) {
			materialiserConnection.rollback();
			throw new MaterialisationFailedException(e);
		}
	}

	public void shutdown() {
		materialiserConnection.shutdown();
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
	 * @param entityIds The subjects of the entities to delete.
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

			materialiserConnection.getStatements(subject, null, null).forEach((Statement statement) -> {
				Value object = statement.getObject();
				if (object.isBNode()) {
					subjectStack.push((Resource) object);
				}
			});

			materialiserConnection.remove(subject, null, null);
		}
	}

	private void commitMembers() {
		materialiserConnection.commit();
		uncommittedMembers = 0;
	}

	private void resetExecutor() {
		scheduledExecutorService.shutdownNow();
		initExecutor();
	}

	private void initExecutor() {
		scheduledExecutorService = newSingleThreadScheduledExecutor();
		scheduledExecutorService.schedule(this::commitMembers, batchTimeout, TimeUnit.MILLISECONDS);
	}

}
