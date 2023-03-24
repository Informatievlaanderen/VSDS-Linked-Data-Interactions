package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.http.client.HttpClient;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryConfigException;
import org.eclipse.rdf4j.repository.manager.RepositoryInfo;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.junit.jupiter.api.BeforeEach;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

public class RepositoryManagerIT {

	protected RepositoryManager subject;

	@BeforeEach
	public void setUp() throws Exception {
		subject = new RepositoryManager() {

			@Override
			public void setHttpClient(HttpClient httpClient) {
				return;
			}

			@Override
			public URL getLocation() throws MalformedURLException {
				return null;
			}

			@Override
			public HttpClient getHttpClient() {
				return null;
			}

			@Override
			public Collection<RepositoryInfo> getAllRepositoryInfos() throws RepositoryException {
				return null;
			}

			@Override
			protected Repository createRepository(String id) throws RepositoryConfigException, RepositoryException {
				return null;
			}

			@Override
			public RepositoryConfig getRepositoryConfig(String repositoryID)
					throws RepositoryConfigException, RepositoryException {
				return null;
			}

			@Override
			public void addRepositoryConfig(RepositoryConfig config)
					throws RepositoryException, RepositoryConfigException {

			}
		};
	}
}
