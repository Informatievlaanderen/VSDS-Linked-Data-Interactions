package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.eclipse.rdf4j.common.transaction.IsolationLevels;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.CustomHTTPRepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;

/**
 * Wrapper around the RDF4J RepositoryConnection to isolate it more and give to maintenance of the connection more out of hands
 */
public class RepositoryConnectionHolder {
	private final RepositoryManager repositoryManager;
	private final String repositoryId;
	private RepositoryConnection connection;

	public RepositoryConnectionHolder(RepositoryManager repositoryManager, String repositoryId) {
		this.repositoryManager = repositoryManager;
		this.repositoryId = repositoryId;
	}

	public synchronized RepositoryConnection getConnection() {
		if (connection == null || !connection.isOpen()) {
			final Repository repository = repositoryManager.getRepository(repositoryId);
			connection = repository instanceof HTTPRepository
					? new CustomHTTPRepositoryConnection(repository)
					: repository.getConnection();

			connection.begin(IsolationLevels.READ_UNCOMMITTED);
		}
		return connection;
	}

	public void shutdown() {
		if (connection != null) {
			connection.close();
		}
		repositoryManager.shutDown();
	}
}
