package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.MaterialisationFailedException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.JenaToRDF4JConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ModelSubjectsExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.RepositorySinkConnection;
import org.apache.jena.rdf.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Set;

/**
 * Component that will write linked data models to a specified triples store or RDF repository
 */
public class RepositorySink {
	private final RepositorySinkConnection repositorySinkConnection;

	public RepositorySink(String hostUrl, String repositoryId, String namedGraph) {
		this(new RemoteRepositoryManager(hostUrl), repositoryId, namedGraph);
	}

	public RepositorySink(RepositoryManager repositoryManager, String repositoryId, String namedGraph) {
		this.repositorySinkConnection = new RepositorySinkConnection(repositoryManager, repositoryId, namedGraph);
	}

	protected RepositorySinkConnection getRepositoryConnection() {
		return repositorySinkConnection;
	}

	public void process(List<Model> jenaModels) {
		synchronized (repositorySinkConnection) {
			try {
				jenaModels.stream().map(JenaToRDF4JConverter::convert).forEach(updateModel -> {
					deleteEntity(updateModel);
					repositorySinkConnection.add(updateModel);
				});
				repositorySinkConnection.commit();
			} catch (Exception e) {
				repositorySinkConnection.rollback();
				throw new MaterialisationFailedException(e);
			}
		}
	}

	public void closeConnection() {
		repositorySinkConnection.close();
	}

	public void shutdown() {
		repositorySinkConnection.shutdown();
	}

	protected void deleteEntity(org.eclipse.rdf4j.model.Model model) {
		getAllSubjectsFromModel(model)
				.forEach(subject -> repositorySinkConnection.remove(subject, null, null));
	}

	private Set<Resource> getAllSubjectsFromModel(org.eclipse.rdf4j.model.Model model) {
		final Set<Resource> subjects = ModelSubjectsExtractor.extractSubjects(model);
		final Deque<Resource> subjectStack = new ArrayDeque<>(subjects);

		while (!subjectStack.isEmpty()) {
			Resource subject = subjectStack.pop();

			repositorySinkConnection.getStatements(subject, null, null).forEach(statement -> {
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
