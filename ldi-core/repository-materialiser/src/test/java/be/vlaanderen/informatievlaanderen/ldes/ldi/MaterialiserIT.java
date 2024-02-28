package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ModelSubjectsExtractor;
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
	private static final String LOCAL_SERVER_URL = "http://localhost:8080/rdf4j-server";
	private static final String LOCAL_REPOSITORY_ID = "test";
	private static Materialiser materialiser;
	@TempDir
	File dataDir;

	private static final String[] TEST_FILES = new String[]{
			"src/test/resources/people_data_01.nq",
			"src/test/resources/people_data_02.nq"};
	private static final String CHANGED_FILE = "src/test/resources/people_data_03.nq";

	@BeforeEach
	public void setUp() {
		materialiser = new Materialiser(LOCAL_SERVER_URL, LOCAL_REPOSITORY_ID, "");
		final RepositoryManager subject = new LocalRepositoryManager(dataDir);
		subject.init();

		subject.addRepositoryConfig(
				new RepositoryConfig(LOCAL_REPOSITORY_ID, new SailRepositoryConfig(new MemoryStoreConfig(true))));

		materialiser = new Materialiser(subject, LOCAL_REPOSITORY_ID, "");
	}

	@AfterEach
	public void tearDown() {
		materialiser.shutdown();
	}

	@Test
	void when_DataPresent_Then_GetEntityIds() throws Exception {
		var updateModel = Rio.parse(new FileInputStream(TEST_FILES[0]), "", RDFFormat.NQUADS);

		Set<Resource> entityIds = ModelSubjectsExtractor.extractSubjects(updateModel);

		assertThat(entityIds)
				.as("Expected all subjects from test data")
				.hasSize(2);
	}

	@Test
	void when_DeleteEntities_Then_EntitiesRemovedFromStore() throws Exception {
		populateAndCheckRepository(List.of(TEST_FILES));
		Model updateModel = Rio.parse(new FileInputStream(TEST_FILES[0]), "", RDFFormat.NQUADS);
		Model updateModel2 = Rio.parse(new FileInputStream(TEST_FILES[1]), "", RDFFormat.NQUADS);
		Set<Resource> entityIds = ModelSubjectsExtractor.extractSubjects(updateModel);

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

		List<org.apache.jena.rdf.model.Model> models = RDFParser.source(CHANGED_FILE).toModel().listStatements().toList().stream()
				.map(statement -> ModelFactory.createDefaultModel().add(statement))
				.toList();
		materialiser.process(models);

		List<Statement> statements = materialiser.getMaterialiserConnection()
				.getStatements(null, null, null).stream().toList();

		assertThat(testModelInStatements(changedModel, statements)).isTrue();
		assertThat(testModelInStatements(updateModel, statements)).isFalse();
	}

	void populateAndCheckRepository(List<String> files) throws IOException {
		List<Model> models = new ArrayList<>();
		for (String testFile : files) {
			var model = Rio.parse(new FileInputStream(testFile), "", RDFFormat.NQUADS);
			materialiser.getMaterialiserConnection().add(model);
			models.add(model);
		}

		List<Statement> statements = materialiser.getMaterialiserConnection().getStatements(null, null, null).stream().toList();

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
