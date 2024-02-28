package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.MaterialisationFailedException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.JenaToRDF4JConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ModelSubjectsExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.MaterialiserConnection;
import org.apache.jena.rdf.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;

import java.util.*;

public class Materialiser {
	private final MaterialiserConnection materialiserConnection;

	public Materialiser(String hostUrl, String repositoryId, String namedGraph) {
		this(new RemoteRepositoryManager(hostUrl), repositoryId, namedGraph);
	}

	public Materialiser(RepositoryManager repositoryManager, String repositoryId, String namedGraph) {
		this.materialiserConnection = new MaterialiserConnection(repositoryManager, repositoryId, namedGraph);
	}

	protected MaterialiserConnection getMaterialiserConnection() {
		return materialiserConnection;
	}

	public void process(List<Model> jenaModels) {
		try {
			jenaModels.stream()
					.map(JenaToRDF4JConverter::convert)
					.forEach(updateModel -> {
						Set<Resource> entityIds = ModelSubjectsExtractor.extractSubjects(updateModel);
						deleteEntitiesFromRepo(entityIds);
						materialiserConnection.add(updateModel);
					});
			materialiserConnection.commit();
		} catch (Exception e) {
			materialiserConnection.rollback();
			throw new MaterialisationFailedException(e, jenaModels);
		}
	}

	public void shutdown() {
		materialiserConnection.shutdown();
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
}
