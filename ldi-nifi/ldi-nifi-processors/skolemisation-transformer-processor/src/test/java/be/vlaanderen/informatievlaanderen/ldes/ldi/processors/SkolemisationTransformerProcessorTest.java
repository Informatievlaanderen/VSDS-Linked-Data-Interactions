package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.SkolemisationTransformerProperties;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SkolemisationTransformerProcessorTest {
	private TestRunner testRunner;

	@BeforeEach
	void setUp() {
		testRunner = TestRunners.newTestRunner(SkolemisationTransformerProcessor.class);
	}

	@Test
	void test_WrongPropertyName() {
		testRunner.setProperty("SKOLEMIZATION_DOMAIN", "http://example.com");

		assertThatThrownBy(() -> testRunner.run())
				.isInstanceOf(AssertionError.class)
				.hasMessageContaining("Processor has 2 validation failures:");
	}

	@ParameterizedTest
	@EmptySource
	@ValueSource(strings = {" ", "http://"})
	void test_InvalidProperties(String skolemDomain) {
		testRunner.setProperty(SkolemisationTransformerProperties.SKOLEM_DOMAIN, skolemDomain);

		assertThatThrownBy(() -> testRunner.run())
				.isInstanceOf(AssertionError.class)
				.hasMessageContaining("Processor has 1 validation failures");
	}

	@Test
	void name() throws URISyntaxException, IOException {
		Lang mimetype = Lang.TURTLE;
		testRunner.setProperty("SKOLEM_DOMAIN", "http://example.com");

		URI uri = Objects.requireNonNull(getClass().getClassLoader().getResource("mob-hind-model.ttl")).toURI();
		testRunner.enqueue(Path.of(uri), Map.of("mime.type", mimetype.getHeaderString()));

		testRunner.run();

		MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(SUCCESS).getFirst();
		Model result = RDFParser.create().source(flowFile.getContentStream()).lang(mimetype).toModel();

		assertThat(result).has(noBlankNodes());
	}

	private Condition<Model> noBlankNodes() {
		final Predicate<Statement> isStmtWithBNode = statement -> statement.getSubject().isAnon() || statement.getObject().isAnon();
		return new Condition<>(
				model -> !model.listStatements().filterKeep(isStmtWithBNode).hasNext(),
				"Model cannot have blank nodes"
		);
	}
}