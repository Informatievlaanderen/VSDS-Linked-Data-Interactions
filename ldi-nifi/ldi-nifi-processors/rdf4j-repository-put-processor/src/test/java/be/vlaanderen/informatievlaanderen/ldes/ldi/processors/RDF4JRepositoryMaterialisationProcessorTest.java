package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.FileInputStream;

import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RDF4JRepositoryPutMaterialisationProcessorProperties.REPOSITORY_ID;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RDF4JRepositoryPutMaterialisationProcessorProperties.SIMULTANEOUS_FLOWFILES_TO_PROCESS;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RDF4JRepositoryPutMaterialisationProcessorProperties.SPARQL_HOST;

public class RDF4JRepositoryMaterialisationProcessorTest {

	private final String repositoryUrl = "http://localhost:7474/";
	private final String repositoryId = "test-db";

	private TestRunner testRunner;
	private RepositoryManager manager;
	private Repository repo;

	@BeforeEach
	public void init() throws Exception {
		testRunner = TestRunners.newTestRunner(RDF4JRepositoryMaterialisationProcessor.class);

		testRunner.setProperty(SPARQL_HOST, repositoryUrl);
		testRunner.setProperty(REPOSITORY_ID, repositoryId);
		testRunner.setProperty(SIMULTANEOUS_FLOWFILES_TO_PROCESS, "3");
		
		manager = new RemoteRepositoryManager(testRunner.getProcessContext().getProperty(SPARQL_HOST).getValue());
		repo = manager.getRepository(testRunner.getProcessContext().getProperty(REPOSITORY_ID).getValue());
	}

//	 @Test
//	 void testRepository() {
//		 try (RepositoryConnection connection = repo.getConnection()) {
//			 RepositoryResult<Resource> resources = connection.getContextIDs();
//			
//			 assertFalse(resources.hasNext(), "The repository should not contain any resources on initialisation");
//		 }
//	 }
	
	@Test
	void insertRecords() throws Exception {
		testRunner.enqueue(new FileInputStream(new File("/src/main/resources/people-data.nq")));
		
		testRunner.run();
	}
}
