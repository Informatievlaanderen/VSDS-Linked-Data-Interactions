package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;

public class MaterialiserConnection {
	private final String namedGraph;
	private final RepositoryConnectionHolder holder;

	public MaterialiserConnection(RepositoryManager repositoryManager, String repositoryId, String namedGraph) {
		this.namedGraph = namedGraph;
		this.holder = new RepositoryConnectionHolder(repositoryManager, repositoryId);
	}


	public void add(Model model) {
		if (namedGraph != null && !namedGraph.isEmpty()) {
			var namedGraphIRI = SimpleValueFactory.getInstance().createIRI(namedGraph);
			holder.getConnection().add(model, namedGraphIRI);
		} else {
			holder.getConnection().add(model);
		}
	}

	public RepositoryResult<Statement> getStatements(Resource subject, IRI predicate, Value object) {
		return holder.getConnection().getStatements(subject, predicate, object);
	}

	public void remove(Resource subject, IRI predicate, Value object) {
		holder.getConnection().remove(subject, predicate, object);
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

	public void shutdown() {
		holder.shutdown();
	}
}
