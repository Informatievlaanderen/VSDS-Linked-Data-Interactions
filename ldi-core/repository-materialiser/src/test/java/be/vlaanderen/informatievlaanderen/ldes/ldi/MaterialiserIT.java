package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.riot.RDFParser;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.util.Models;
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
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class MaterialiserIT {
	private static final String LOCAL_SERVER_URL = "http://localhost:8080/rdf4j-server";
	private static final String LOCAL_REPOSITORY_ID = "test";
	private static Materialiser materialiser;
	@TempDir
	File dataDir;

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
	void when_DeleteEntities_Then_EntitiesRemovedFromStore() throws Exception {
		final int statementOfModel2And3Count = 9;
		final List<String> testFiles = IntStream.range(1, 4).mapToObj("src/test/resources/people/%d.nq"::formatted).toList();
		populateAndCheckRepository(testFiles);
		final Model modelToDelete = Rio.parse(new FileInputStream("src/test/resources/people/1.nq"), "", RDFFormat.NQUADS);

		materialiser.deleteEntity(modelToDelete);

		List<Statement> statements = materialiser.getMaterialiserConnection()
				.getStatements(null, null, null).stream().toList();

		assertThat(statements).hasSize(statementOfModel2And3Count);
	}

	@Test
	void when_UpdateEntities_Then_OldTriplesRemoved() throws Exception {
		final List<String> testFiles = IntStream.range(1, 6).mapToObj("src/test/resources/people/%d.nq"::formatted).toList();
		populateAndCheckRepository(testFiles);

		List<org.apache.jena.rdf.model.Model> models = List.of(RDFParser.source("people/5-updated.nq").toModel());
		materialiser.process(models);

		List<Statement> statements = materialiser.getMaterialiserConnection()
				.getStatements(null, null, null).stream().toList();

		assertThat(statements)
				.hasSize(21)
				.anyMatch(statement -> statement.getObject().stringValue().equals("CHANGED"))
				.noneMatch(statement -> statement.getObject().stringValue().equals("Taylor"));
	}


	@Test
	void given_ComplexModelToUpdate_test_Process() throws IOException {
		final org.apache.jena.rdf.model.Model originalModel = RDFParser.source("movies/1.ttl").toModel();
		materialiser.process(List.of(originalModel));

		final org.apache.jena.rdf.model.Model updatedJenaModel = RDFParser.source("movies/2.ttl").toModel();
		final Model updatedRdfModel = Rio.parse(new FileInputStream("src/test/resources/movies/2.ttl"), "", RDFFormat.TURTLE);
		materialiser.process(List.of(updatedJenaModel));


		Model updatedDbModel = new LinkedHashModel();
		materialiser.getMaterialiserConnection()
				.getStatements(null, null, null)
				.forEach(updatedDbModel::add);

		assertThat(Models.isomorphic(updatedDbModel, updatedRdfModel)).isTrue();
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

}
