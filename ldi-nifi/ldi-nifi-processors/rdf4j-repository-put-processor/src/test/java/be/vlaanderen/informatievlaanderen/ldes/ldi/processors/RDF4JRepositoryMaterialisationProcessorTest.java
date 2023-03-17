package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileInputStream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RDF4JRepositoryPutMaterialisationProcessorProperties.REPOSITORY_ID;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RDF4JRepositoryPutMaterialisationProcessorProperties.SIMULTANEOUS_FLOWFILES_TO_PROCESS;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RDF4JRepositoryPutMaterialisationProcessorProperties.SPARQL_HOST;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RDF4JRepositoryMaterialisationProcessorTest {

	private final String repositoryUrl = "http://localhost:8080/rdf4j-server/";
	private final String repositoryId = "test-db";
	private final String testFile = "src/test/resources/people_data.nq";

	private TestRunner testRunner;
	private RepositoryManager mockManager;
	private Repository mockRepo;

	@BeforeEach
	public void init() throws Exception {
		testRunner = TestRunners.newTestRunner(RDF4JRepositoryMaterialisationProcessor.class);

		testRunner.setProperty(SPARQL_HOST, repositoryUrl);
		testRunner.setProperty(REPOSITORY_ID, repositoryId);
		testRunner.setProperty(SIMULTANEOUS_FLOWFILES_TO_PROCESS, "3");
		
		mockManager = new RemoteRepositoryManager(repositoryUrl);
		mockRepo = mockManager.getRepository(repositoryId);
		
//		RepositoryManager mockManager = mock(RepositoryManager.class);
//		Repository mockRepo = mock(Repository.class);
//		RepositoryConnection mockConnection = mock(RepositoryConnection.class);
//		
//		when(mockManager.getRepository(repositoryUrl)).thenReturn(mockRepo);
//		when(mockRepo.getConnection()).thenReturn(mockConnection);
		
	}

	@Test
	void testRepository() {
		try (RepositoryConnection connection = mockRepo.getConnection()) {
			RepositoryResult<Resource> resources = connection.getContextIDs();
			assertFalse(resources.hasNext(), "The repository should not contain any resources on initialisation");
		}
	}

	@Test
	void insertRecords() throws Exception {
		testRunner.enqueue(new FileInputStream(new File(testFile)));

		testRunner.run();
	}
}
