package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;

import java.util.List;
import java.util.Optional;

/**
 * Wrapper around the RepositoryConnectionHolder, to make more an abstraction of the actions that can be done
 * through the RepositoryConnection and already handling with some overhead of the named graph
 */
public class RepositorySinkConnection {
	private final String namedGraph;
	private final RepositoryConnectionHolder holder;

	public RepositorySinkConnection(RepositoryManager repositoryManager, String repositoryId, String namedGraph) {
		this.namedGraph = namedGraph;
		this.holder = new RepositoryConnectionHolder(repositoryManager, repositoryId);
	}

	public void add(Model model) {
		getNamedGraphIri().ifPresentOrElse(
				namedGraphIri -> holder.getConnection().add(model, namedGraphIri),
				() -> holder.getConnection().add(model));
	}

	public RepositoryResult<Statement> getStatements(Resource subject, IRI predicate, Value object) {
		return getNamedGraphIri()
				.map(namedGraphIri -> holder.getConnection().getStatements(subject, predicate, object, namedGraphIri))
				.orElseGet(() -> holder.getConnection().getStatements(subject, predicate, object));
	}

	public void remove(List<Statement> statements) {
		getNamedGraphIri().ifPresentOrElse(
				namedGraphIri -> holder.getConnection().remove(statements, namedGraphIri),
				() -> holder.getConnection().remove(statements)
		);
	}
	public void commit() {
		try (RepositoryConnection connection = holder.getConnection()) {
			connection.commit();
		}
	}

	public void rollback() {
		try (RepositoryConnection connection = holder.getConnection()) {
			connection.rollback();
		}
	}

	public void close() {
		holder.getConnection().close();
	}

	public void shutdown() {
		holder.shutdown();
	}

	private Optional<Resource> getNamedGraphIri() {
		return Optional
				.ofNullable(namedGraph)
				.filter(graph -> !graph.isEmpty())
				.map(SimpleValueFactory.getInstance()::createIRI);
	}
}
