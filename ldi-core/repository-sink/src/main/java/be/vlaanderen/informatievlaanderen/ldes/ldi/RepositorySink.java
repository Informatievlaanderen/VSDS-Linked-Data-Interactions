package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.SinkFailedException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.JenaToRDF4JConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ModelSubjectsExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.RepositorySinkConnection;
import org.apache.jena.rdf.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Component that will write linked data models to a specified triples store or RDF repository
 */
public class RepositorySink {
	private static final Pattern SKOLEM_PATTERN = Pattern.compile(".*/.well-known/genid/.*");
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
				throw new SinkFailedException(e);
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
		repositorySinkConnection.remove(getAllOldModelStatements(model));
	}

	private List<Statement> getAllOldModelStatements(org.eclipse.rdf4j.model.Model model) {
		final List<Statement> result = new ArrayList<>(10000); // TODO: check how to better determine initial capacity
		final Deque<Resource> subjectStack = new ArrayDeque<>(ModelSubjectsExtractor.extractSubjects(model));

		while (!subjectStack.isEmpty()) {
			Resource subject = subjectStack.pop();
			repositorySinkConnection.getStatements(subject, null, null).forEach(statement -> {
				result.add(statement);
				Value object = statement.getObject();
				if (object.isBNode() || (object.isIRI() && SKOLEM_PATTERN.matcher(object.stringValue()).matches())) {
					subjectStack.push((Resource) object);
				}
			});
		}
		return result;
	}
}
