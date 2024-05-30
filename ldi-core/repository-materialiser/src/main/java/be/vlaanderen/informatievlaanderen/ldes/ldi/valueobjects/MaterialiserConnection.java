package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;

import java.util.Optional;

/**
 * Wrapper around the RepositoryConnectionHolder, to make more an abstraction of the actions that can be done
 * through the RepositoryConnection and already handling with some overhead of the named graph
 */
public class MaterialiserConnection {
	private final String namedGraph;
	private final RepositoryConnectionHolder holder;

	public MaterialiserConnection(RepositoryManager repositoryManager, String repositoryId, String namedGraph) {
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

	public void remove(Resource subject, IRI predicate, Value object) {
		getNamedGraphIri().ifPresentOrElse(
				namedGraphIri -> holder.getConnection().remove(subject, predicate, object, namedGraphIri),
				() -> holder.getConnection().remove(subject, predicate, object)
		);
	}

	public void commit() {
		final RepositoryConnection connection = holder.getConnection();
		connection.commit();
		connection.close();
	}

	public void rollback() {
		final RepositoryConnection connection = holder.getConnection();
		connection.rollback();
		connection.close();
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
