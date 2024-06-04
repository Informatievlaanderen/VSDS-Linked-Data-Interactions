package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.MaterialisationFailedException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.JenaToRDF4JConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ModelSubjectsExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.MaterialiserConnection;
import org.apache.jena.rdf.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Component that will write linked data models to a specified triples store or RDF repository
 */
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
		synchronized (materialiserConnection) {
			try {
				jenaModels.stream().map(JenaToRDF4JConverter::convert).forEach(updateModel -> {
					deleteEntity(updateModel);
					materialiserConnection.add(updateModel);
				});
				materialiserConnection.commit();
			} catch (Exception e) {
				materialiserConnection.rollback();
				throw new MaterialisationFailedException(e);
			}
		}
	}

	public CompletableFuture<Void> processAsync(List<Model> jenaModels) {
		return CompletableFuture.runAsync(() -> process(jenaModels));
	}

	public void closeConnection() {
		materialiserConnection.close();
	}

	public void shutdown() {
		materialiserConnection.shutdown();
	}

	protected void deleteEntity(org.eclipse.rdf4j.model.Model model) {
		getAllSubjectsFromModel(model)
				.forEach(subject -> materialiserConnection.remove(subject, null, null));
	}

	private Set<Resource> getAllSubjectsFromModel(org.eclipse.rdf4j.model.Model model) {
		final Set<Resource> subjects = ModelSubjectsExtractor.extractSubjects(model);
		final Deque<Resource> subjectStack = new ArrayDeque<>(subjects);

		while (!subjectStack.isEmpty()) {
			Resource subject = subjectStack.pop();

			materialiserConnection.getStatements(subject, null, null).forEach(statement -> {
				Value object = statement.getObject();
				if (object.isBNode()) {
					Resource bnode = (Resource) object;
					subjectStack.push(bnode);
					subjects.add(bnode);
				}
			});

		}
		return subjects;
	}
}
