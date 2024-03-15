package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.riot.RDFParser;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
		final List<String> testFiles = IntStream.range(1, 4).mapToObj("src/test/resources/people/%d.nq"::formatted).toList();
		populateAndCheckRepository(testFiles);
		final Model model = Rio.parse(new FileInputStream("src/test/resources/people/1.nq"), "", RDFFormat.NQUADS);
		Set<Resource> entityIds = Stream.of("http://somewhere/BeckySmith/", "http://somewhere/SarahJones/")
				.map(iri -> SimpleValueFactory.getInstance().createIRI(iri))
				.collect(Collectors.toSet());

		materialiser.deleteEntities(entityIds);

		List<Statement> statements = materialiser.getMaterialiserConnection()
				.getStatements(null, null, null).stream().toList();

		assertThat(statements).hasSize(4);
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
