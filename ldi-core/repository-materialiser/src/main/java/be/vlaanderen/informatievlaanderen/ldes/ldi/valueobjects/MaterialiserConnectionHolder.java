package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.CustomHTTPRepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;

public class MaterialiserConnectionHolder {
	private final RepositoryConnection connection;

	private MaterialiserConnectionHolder(RepositoryConnection connection) {
		this.connection = connection;
	}

	public RepositoryConnection getConnection() {
		return connection;
	}

	public static MaterialiserConnectionHolder initializeFromRepository(Repository repository) {
		final RepositoryConnection repositoryConnection = repository instanceof HTTPRepository
				? new CustomHTTPRepositoryConnection(repository)
				: repository.getConnection();
		return new MaterialiserConnectionHolder(repositoryConnection);
	}

}
