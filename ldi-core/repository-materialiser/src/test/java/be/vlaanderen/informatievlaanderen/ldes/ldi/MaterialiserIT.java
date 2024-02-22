package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.MaterialisationFailedException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.MaterialiserConnection;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFParser;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class MaterialiserIT {
	private static final String LOCAL_REPOSITORY_ID = "test";
	private static final String NAMED_GRAPH = "";
	private static final int BATCH_SIZE = 2;
	private static final int BATCH_TIMEOUT = 120000;

	private static RepositoryManager subject;
	private static Materialiser materialiser;
	@TempDir
	File dataDir;

	private static final String[] TEST_FILES = new String[]{
			"src/test/resources/people_data_01.nq",
			"src/test/resources/people_data_02.nq"};
	private static final String CHANGED_FILE = "src/test/resources/people_data_03.nq";

	@BeforeEach
	public void setUp() {
		subject = new LocalRepositoryManager(dataDir);
		subject.init();

		subject.addRepositoryConfig(
				new RepositoryConfig(LOCAL_REPOSITORY_ID, new SailRepositoryConfig(new MemoryStoreConfig(true))));

		materialiser = new Materialiser(subject, LOCAL_REPOSITORY_ID, NAMED_GRAPH, BATCH_SIZE, BATCH_TIMEOUT);
	}

	@AfterEach
	public void tearDown() {
		materialiser.shutdown();
		subject.shutDown();
	}

	@Test
	void when_DataPresent_Then_GetEntityIds() throws Exception {
		var updateModel = Rio.parse(new FileInputStream(TEST_FILES[0]), "", RDFFormat.NQUADS);

		Set<Resource> entityIds = Materialiser.getSubjectsFromModel(updateModel);

		assertThat(entityIds)
				.as("Expected all subjects from test data")
				.hasSize(2);
	}

	@Test
	void when_DeleteEntities_Then_EntitiesRemovedFromStore() throws Exception {
		populateAndCheckRepository(List.of(TEST_FILES));
		Model updateModel = Rio.parse(new FileInputStream(TEST_FILES[0]), "", RDFFormat.NQUADS);
		Model updateModel2 = Rio.parse(new FileInputStream(TEST_FILES[1]), "", RDFFormat.NQUADS);
		Set<Resource> entityIds = Materialiser.getSubjectsFromModel(updateModel);

		materialiser.deleteEntitiesFromRepo(entityIds);

		List<Statement> statements = materialiser.getMaterialiserConnection()
				.getStatements(null, null, null).stream().toList();

		assertThat(statements)
				.containsExactlyInAnyOrderElementsOf(updateModel2)
				.doesNotContainAnyElementsOf(updateModel);
	}

	@Test
	void when_UpdateEntities_Then_OldTriplesRemoved() throws Exception {
		populateAndCheckRepository(List.of(TEST_FILES));
		Model updateModel = Rio.parse(new FileInputStream(TEST_FILES[0]), "", RDFFormat.NQUADS);
		Model changedModel = Rio.parse(new FileInputStream(CHANGED_FILE), "", RDFFormat.NQUADS);

		RDFParser.source(CHANGED_FILE).toModel().listStatements().toList().stream()
				.map(statement -> ModelFactory.createDefaultModel().add(statement))
				.forEach(materialiser::process);

		List<Statement> statements = materialiser.getMaterialiserConnection()
				.getStatements(null, null, null).stream().toList();

		assertThat(testModelInStatements(updateModel, statements)).isFalse();
		assertThat(testModelInStatements(changedModel, statements)).isTrue();
	}

	@Test
	void when_ErrorOccurs_then_ChangesAreRolledBack() throws IOException {
		populateAndCheckRepository(List.of(TEST_FILES));
		org.apache.jena.rdf.model.Model jenaModel = ModelFactory.createDefaultModel().add(
				RDFParser.source(CHANGED_FILE).toModel().listStatements()
		);

		materialiser.process(jenaModel);
		try {
			materialiser.process(null);
		} catch (MaterialisationFailedException e) {
			assertThat(e).hasCauseInstanceOf(NullPointerException.class);
		}

		List<Statement> statements = materialiser.getMaterialiserConnection()
				.getStatements(null, null, null).stream().toList();

		assertThat(statements)
				.hasSize(4)
				.noneMatch(statement -> statement.getObject().stringValue().equals("Changed"));

	}

	void populateAndCheckRepository(List<String> files) throws IOException {
		final MaterialiserConnection connection = materialiser.getMaterialiserConnection();
		List<Model> models = new ArrayList<>();
		for (String testFile : files) {
			var model = Rio.parse(new FileInputStream(testFile), "", RDFFormat.NQUADS);
			connection.add(model);
			models.add(model);
		}
		connection.commit();

		List<Statement> statements = connection.getStatements(null, null, null).stream().toList();

		assertThat(models).allMatch(statements::containsAll);
	}

	private boolean testModelInStatements(Model model, List<Statement> statements) {
		AtomicBoolean result = new AtomicBoolean(true);
		model.getStatements(null, null, null).forEach(statement -> {
			if (!statements.contains(statement)) {
				result.set(false);
			}
		});
		return result.get();
	}

}
