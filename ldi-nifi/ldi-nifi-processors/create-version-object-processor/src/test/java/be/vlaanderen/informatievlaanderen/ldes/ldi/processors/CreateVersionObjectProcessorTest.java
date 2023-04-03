package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.CreateVersionObjectProcessorRelationships.DATA_RELATIONSHIP;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.CreateVersionObjectProcessorRelationships.DATA_UNPARSEABLE_RELATIONSHIP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateVersionObjectProcessorTest {

	private static final String DEFAULT_DATE_OBSERVED_VALUE_RDF_PROPERTY = "https://uri.etsi.org/ngsi-ld/observedAt";
	private static final String DEFAULT_MEMBER_TYPE_WQO = "https://uri.etsi.org/ngsi-ld/default-context/WaterQualityObserved";
	private static final String DEFAULT_DELIMITER = "/";
	private static final String DEFAULT_VERSION_OF_KEY = "http://purl.org/dc/terms/isVersionOf";
	private static final String DEFAULT_DATA_DESTINATION_FORMAT = "n-quads";
	private static final String DEFAULT_PROV_GENERATED_AT_TIME = "http://www.w3.org/ns/prov#generatedAtTime";

	private TestRunner testRunner;

	@BeforeEach
	public void init() {
		testRunner = TestRunners.newTestRunner(CreateVersionObjectProcessor.class);
	}

	@Test
	void when_InputIsValidJsonLD_ExpectedVersionObjectIsReturned() throws IOException, URISyntaxException {
		testRunner.setProperty("DATE_OBSERVED_VALUE_RDF_PROPERTY", DEFAULT_DATE_OBSERVED_VALUE_RDF_PROPERTY);
		testRunner.setProperty("MEMBER_RDF_SYNTAX_TYPE", DEFAULT_MEMBER_TYPE_WQO);
		testRunner.setProperty("DELIMITER", DEFAULT_DELIMITER);
		testRunner.setProperty("VERSION_OF_KEY", DEFAULT_VERSION_OF_KEY);
		testRunner.setProperty("DATA_DESTINATION_FORMAT", DEFAULT_DATA_DESTINATION_FORMAT);
		testRunner.setProperty("GENERATED_AT_TIME_PROPERTY", "");

		final Path JSON_SNIPPET = Paths.get(String.valueOf(new File(
				Objects.requireNonNull(getClass().getClassLoader().getResource("example-waterqualityobserved.json"))
						.toURI())));
		testRunner.enqueue(JSON_SNIPPET);
		testRunner.run(1);

		List<MockFlowFile> dataFlowfiles = testRunner.getFlowFilesForRelationship(DATA_RELATIONSHIP);
		assertEquals(1, dataFlowfiles.size());
		assertEquals("application/n-quads", dataFlowfiles.get(0).getAttribute("mime.type"));
		String content = dataFlowfiles.get(0).getContent();
		Model model = readLdesMemberFromFile(getClass().getClassLoader(), "expected-waterqualityobserved-n-quads.nq");
		assertTrue(model.isIsomorphicWith(getModel(content, Lang.NQUADS)));

	}

	@Test
	void when_InputIsInvalidJsonLD_ExpectedVersionObjectIsReturned() throws IOException, URISyntaxException {
		testRunner.setProperty("DATE_OBSERVED_VALUE_RDF_PROPERTY", DEFAULT_DATE_OBSERVED_VALUE_RDF_PROPERTY);
		testRunner.setProperty("MEMBER_RDF_SYNTAX_TYPE", DEFAULT_MEMBER_TYPE_WQO);
		testRunner.setProperty("DELIMITER", DEFAULT_DELIMITER);
		testRunner.setProperty("VERSION_OF_KEY", DEFAULT_VERSION_OF_KEY);
		testRunner.setProperty("DATA_DESTINATION_FORMAT", DEFAULT_DATA_DESTINATION_FORMAT);
		testRunner.setProperty("GENERATED_AT_TIME_PROPERTY", DEFAULT_PROV_GENERATED_AT_TIME);

		final Path JSON_SNIPPET = Paths.get(String.valueOf(new File(
				Objects.requireNonNull(
						getClass().getClassLoader().getResource("example-invalid-waterqualityobserved.json"))
						.toURI())));
		testRunner.enqueue(JSON_SNIPPET);
		testRunner.run(1);

		List<MockFlowFile> dataFlowfiles = testRunner.getFlowFilesForRelationship(DATA_UNPARSEABLE_RELATIONSHIP);
		assertEquals(1, dataFlowfiles.size());
	}

	@Test
	void when_InputIsValidJsonLDAndContainsGMLData_ExpectedVersionObjectIsReturned()
			throws URISyntaxException, IOException {
		testRunner.setProperty("DATE_OBSERVED_VALUE_RDF_PROPERTY", DEFAULT_DATE_OBSERVED_VALUE_RDF_PROPERTY);
		testRunner.setProperty("MEMBER_RDF_SYNTAX_TYPE", "https://data.vlaanderen.be/ns/adres#Adres");
		testRunner.setProperty("DELIMITER", DEFAULT_DELIMITER);
		testRunner.setProperty("VERSION_OF_KEY", DEFAULT_VERSION_OF_KEY);
		testRunner.setProperty("DATA_DESTINATION_FORMAT", "application/ld+json");
		final Path JSON_SNIPPET = Paths.get(String.valueOf(new File(
				Objects.requireNonNull(getClass().getClassLoader().getResource("example-address-gml.json"))
						.toURI())));
		testRunner.enqueue(JSON_SNIPPET);
		testRunner.run(1);

		List<MockFlowFile> dataFlowfiles = testRunner.getFlowFilesForRelationship(DATA_RELATIONSHIP);

		assertEquals(1, dataFlowfiles.size());
	}

	private Model readLdesMemberFromFile(ClassLoader classLoader, String fileName)
			throws URISyntaxException, IOException {
		File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());

		return getModel(Files.lines(Paths.get(file.toURI())).collect(Collectors.joining()), Lang.NQUADS);
	}

	private Model getModel(String s, Lang lang) {
		return RDFParserBuilder.create()
				.fromString(s).lang(lang)
				.toModel();
	}

}
