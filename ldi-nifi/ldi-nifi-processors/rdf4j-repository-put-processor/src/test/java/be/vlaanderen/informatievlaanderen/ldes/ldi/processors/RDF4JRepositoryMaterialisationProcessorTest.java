package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RDF4JRepositoryPutMaterialisationProcessorProperties.REPOSITORY_ID;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RDF4JRepositoryPutMaterialisationProcessorProperties.SIMULTANEOUS_FLOWFILES_TO_PROCESS;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RDF4JRepositoryPutMaterialisationProcessorProperties.SPARQL_HOST;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;

import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RDF4JRepositoryMaterialisationProcessorTest {

	private final String repositoryUrl = "http://localhost:8080/rdf4j-server/";
	private final String repositoryId = "test";
	private final int simultaneousFlowfiles = 3;
	
	private final String[] testFiles = new String[] {
			"src/test/resources/people_data_01.nq",
			"src/test/resources/people_data_02.nq",
			"src/test/resources/people_data_03.nq",
			"src/test/resources/people_data_04.nq"
	};

	private TestRunner testRunner;
	private RepositoryManager mockManager;
	private Repository mockRepo;

	@BeforeEach
	public void init() throws Exception {
		testRunner = TestRunners.newTestRunner(RDF4JRepositoryMaterialisationProcessor.class);

		testRunner.setProperty(SPARQL_HOST, repositoryUrl);
		testRunner.setProperty(REPOSITORY_ID, repositoryId);
		testRunner.setProperty(SIMULTANEOUS_FLOWFILES_TO_PROCESS, simultaneousFlowfiles + "");

		mockManager = new RemoteRepositoryManager(repositoryUrl);
		mockRepo = mockManager.getRepository(repositoryId);

		// RepositoryManager mockManager = mock(RepositoryManager.class);
		// Repository mockRepo = mock(Repository.class);
		// RepositoryConnection mockConnection = mock(RepositoryConnection.class);
		//
		// when(mockManager.getRepository(repositoryUrl)).thenReturn(mockRepo);
		// when(mockRepo.getConnection()).thenReturn(mockConnection);

	}

	@Test
	void testRepository() {
		assertNotNull(mockManager, "RepositoryManager instance is not null");
		assertNotNull(mockRepo, "Repository instance is not null");
		
		boolean commandExecutedWithoutException = false;
		try (RepositoryConnection connection = mockRepo.getConnection()) {
			connection.getContextIDs();
			commandExecutedWithoutException = true;
		}
		assertTrue(commandExecutedWithoutException, "The system connects to the rdf4j server and can execute commands");
	}

	@Test
	void transferRecords() throws Exception {
		for (String testFile : testFiles) {
			testRunner.enqueue(new FileInputStream(new File(testFile)));
		}
		
		testRunner.run(1);
		
		assertEquals(simultaneousFlowfiles, testRunner.getFlowFilesForRelationship(SUCCESS).size(), "All members successfully transferred");
		assertEquals(0, testRunner.getFlowFilesForRelationship(FAILURE).size(), "No failures on flowfile transmission");
		
		testRunner.run(1);
		
		assertEquals(testFiles.length, testRunner.getFlowFilesForRelationship(SUCCESS).size(), "All members successfully transferred");
		assertEquals(0, testRunner.getFlowFilesForRelationship(FAILURE).size(), "No failures on flowfile transmission");
	}
}
