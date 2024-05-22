package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.VersionMaterialisationProcessorProperties.VERSION_OF_PROPERTY;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.VersionMaterialisationProcessorProperties.RESTRICT_TO_MEMBERS;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionMaterialiseProcessorTest {
	private TestRunner testRunner;

	@BeforeEach
	void init() {
		testRunner = TestRunners.newTestRunner(VersionMaterialiseProcessor.class);
	}

	@Test
	void testVersionMaterialiseMemberOnly() throws IOException {
		testRunner.setProperty(VERSION_OF_PROPERTY,
				"http://purl.org/dc/terms/isVersionOf");
		testRunner.setProperty(RESTRICT_TO_MEMBERS, "true");

		String versionedMember = Files.readString(Path.of("src/test/resources/ldes-member-versioned.ttl"));
		testRunner.enqueue(versionedMember, Map.of("mime.type", "text/turtle"));

		testRunner.run();

		MockFlowFile flowFileOut = testRunner.getFlowFilesForRelationship(SUCCESS)
				.get(0);
		Model flowFileOutModel = RDFParser.create()
				.source(flowFileOut.getContentStream())
				.lang(Lang.TURTLE)
				.toModel();
		InputStream comparisonFile = new FileInputStream("src/test/resources/ldes-member-unversioned.ttl");
		Model comparisonModel = RDFParser.create()
				.source(comparisonFile)
				.lang(Lang.TURTLE)
				.toModel();

		assertTrue(flowFileOutModel.isIsomorphicWith(comparisonModel));
	}

	@Test
	void testVersionMaterialiseWithContext() throws IOException {
		testRunner.setProperty(VERSION_OF_PROPERTY, "http://purl.org/dc/terms/isVersionOf");
		testRunner.setProperty(RESTRICT_TO_MEMBERS, "false");

		String versionedMember = Files.readString(Path.of("src/test/resources/ldes-member-versioned.ttl"));
		testRunner.enqueue(versionedMember, Map.of("mime.type", "text/turtle"));

		testRunner.run();

		MockFlowFile flowFileOut = testRunner.getFlowFilesForRelationship(SUCCESS)
				.get(0);
		Model flowFileOutModel = RDFParser.create()
				.source(flowFileOut.getContentStream())
				.lang(Lang.TURTLE)
				.toModel();
		InputStream comparisonFile = new FileInputStream(
				"src/test/resources/ldes-member-unversioned-context-included.ttl");
		Model comparisonModel = RDFParser.create()
				.source(comparisonFile)
				.lang(Lang.TURTLE)
				.toModel();

		assertTrue(flowFileOutModel.isIsomorphicWith(comparisonModel));
	}

}
