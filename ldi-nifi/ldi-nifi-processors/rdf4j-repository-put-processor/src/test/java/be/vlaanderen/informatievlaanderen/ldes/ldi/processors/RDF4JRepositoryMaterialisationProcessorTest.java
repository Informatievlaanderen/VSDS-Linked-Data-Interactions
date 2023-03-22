package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.config.MemoryStoreConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RDF4JRepositoryPutMaterialisationProcessorProperties.REPOSITORY_ID;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RDF4JRepositoryPutMaterialisationProcessorProperties.SIMULTANEOUS_FLOWFILES_TO_PROCESS;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RDF4JRepositoryPutMaterialisationProcessorProperties.SPARQL_HOST;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the RDF4JRepositoryMaterialisationProcessor
 *
 * @see https://github.com/eclipse/rdf4j/blob/main/compliance/repository/src/test/java/org/eclipse/rdf4j/repository/manager/LocalRepositoryManagerIntegrationTest.java
 *
 */
public class RDF4JRepositoryMaterialisationProcessorTest extends RepositoryManagerIT {

	private static final String LOCAL_SERVER_URL = "http://localhost:8080/rdf4j-server";
	private static final String LOCAL_REPOSITORY_ID = "test";
	private static final int SIMULTANEOUS_FLOWFILES = 3;

	@TempDir
	File dataDir;

	private static final String[] TEST_FILES = new String[] {
			"src/test/resources/people_data_01.nq",
			"src/test/resources/people_data_02.nq",
			"src/test/resources/people_data_03.nq",
			"src/test/resources/people_data_04.nq" };
	private static final String CHANGED_FILE = "src/test/resources/people_data_05.nq";

	private TestRunner testRunner;

	@BeforeEach
	public void setUp() throws Exception {
		subject = new LocalRepositoryManager(dataDir);
		subject.init();

		subject.addRepositoryConfig(
				new RepositoryConfig(LOCAL_REPOSITORY_ID, new SailRepositoryConfig(new MemoryStoreConfig(true))));

		testRunner = TestRunners.newTestRunner(RDF4JRepositoryMaterialisationProcessor.class);

		testRunner.setProperty(SPARQL_HOST, LOCAL_SERVER_URL);
		testRunner.setProperty(REPOSITORY_ID, LOCAL_REPOSITORY_ID);
		testRunner.setProperty(SIMULTANEOUS_FLOWFILES_TO_PROCESS, SIMULTANEOUS_FLOWFILES + "");

		((RDF4JRepositoryMaterialisationProcessor) (testRunner.getProcessor())).setRepositoryManager(subject);
	}

	@AfterEach
	public void tearDown() throws Exception {
		subject.shutDown();
	}

	@Test
	void testRepository() {
		Repository mockRepo = subject
				.getRepository(testRunner.getProcessContext().getProperty(REPOSITORY_ID).getValue());

		assertNotNull(subject, "RepositoryManager instance is not null");
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
		for (String testFile : TEST_FILES) {
			testRunner.enqueue(new FileInputStream(new File(testFile)));
		}

		testRunner.run(1);

		assertEquals(SIMULTANEOUS_FLOWFILES, testRunner.getFlowFilesForRelationship(SUCCESS).size(),
				"All members successfully transferred");
		assertEquals(0, testRunner.getFlowFilesForRelationship(FAILURE).size(), "No failures on flowfile transmission");

		testRunner.run(1);

		assertEquals(TEST_FILES.length, testRunner.getFlowFilesForRelationship(SUCCESS).size(),
				"All members successfully transferred");
		assertEquals(0, testRunner.getFlowFilesForRelationship(FAILURE).size(), "No failures on flowfile transmission");
	}

	@Test
	void testGetSubjectsFromModel() throws Exception {
		for (String testFile : TEST_FILES) {
			var updateModel = Rio.parse(new FileInputStream(new File(testFile)), "", RDFFormat.NQUADS);

			Set<Resource> entityIds = RDF4JRepositoryMaterialisationProcessor.getSubjectsFromModel(updateModel);

			assertEquals(4, entityIds.size(), "Expected all subjects from test data");
		}
	}

	@Test
	void testDeleteSubjectsFromRepo() throws Exception {
		for (String testFile : TEST_FILES) {
			var updateModel = Rio.parse(new FileInputStream(new File(testFile)), "", RDFFormat.NQUADS);

			Set<Resource> entityIds = RDF4JRepositoryMaterialisationProcessor.getSubjectsFromModel(updateModel);

			RDF4JRepositoryMaterialisationProcessor.deleteEntitiesFromRepo(entityIds,
					subject.getRepository(LOCAL_REPOSITORY_ID).getConnection());
		}
	}

	@Test
	void recordsAreUpdated() throws Exception {
		for (String testFile : TEST_FILES) {
			testRunner.enqueue(new FileInputStream(new File(testFile)));
		}

		testRunner.run(2);

		RepositoryConnection connection = subject.getRepository(LOCAL_REPOSITORY_ID).getConnection();
		String queryString = "select ?given_name where { " +
				"<http://somewhere/TaylorKennedy/> <http://www.w3.org/2001/vcard-rdf/3.0#N> ?id . " +
				"?id <http://www.w3.org/2001/vcard-rdf/3.0#Given> ?given_name " +
				"}";
		TupleQuery tupleQuery = connection.prepareTupleQuery(queryString);

		try (TupleQueryResult result = tupleQuery.evaluate()) {
			if (result.hasNext()) {
				BindingSet bindingSet = result.next();

				assertEquals("Taylor", bindingSet.getValue("given_name").stringValue(),
						"Expecting original value for first name of Taylor Kennedy");
			}
		}

		testRunner.enqueue(new FileInputStream(new File(CHANGED_FILE)));
		testRunner.run(1);

		try (TupleQueryResult result = tupleQuery.evaluate()) {
			if (result.hasNext()) {
				BindingSet bindingSet = result.next();

				assertEquals("CHANGED", bindingSet.getValue("given_name").stringValue(),
						"Expecting changed value for first name of Taylor Kennedy");
			}
		}
	}
}
