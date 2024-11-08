package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.ConfigPropertyMissingException;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpSparqlOutProperties.*;
import static org.assertj.core.api.Assertions.*;

class LdioHttpSparqlOutPropertiesTest {
	@Test
	void test_Defaults() {
		final String endpoint = "http://localhost:8888/sparql";
		final ComponentProperties componentProperties = new ComponentProperties("test-pipeline", "cname", Map.of("endpoint", endpoint));

		final LdioHttpSparqlOutProperties httpSparqlOutProperties = new LdioHttpSparqlOutProperties(componentProperties);

		assertThat(httpSparqlOutProperties)
				.hasFieldOrPropertyWithValue("endpoint", endpoint)
				.has(emptyProperty(LdioHttpSparqlOutProperties::getSkolemisationDomain))
				.has(emptyProperty(LdioHttpSparqlOutProperties::getGraph))
				.is(replacementEnabled())
				.has(replacementDepth(10))
				.has(noReplacementDeleteFunction());
	}

	@Test
	void given_MissingEndpoint_when_GetEndpoint_then_ThrowException() {
		final ComponentProperties componentProperties = new ComponentProperties("test-pipeline", "cname");

		final LdioHttpSparqlOutProperties httpSparqlOutProperties = new LdioHttpSparqlOutProperties(componentProperties);

		assertThatThrownBy(httpSparqlOutProperties::getEndpoint)
				.isInstanceOf(ConfigPropertyMissingException.class)
				.hasMessage("Pipeline \"test-pipeline\": \"cname\" : Missing value for property \"endpoint\" .");
	}

	@Test
	void test_CustomSkolemDomain() {
		final Map<String, String> properties = Map.of(SKOLEMISATION_SKOLEM_DOMAIN, "http://example.org");
		final ComponentProperties componentProperties = new ComponentProperties("test-pipeline", "cname", properties);

		final LdioHttpSparqlOutProperties httpSparqlOutProperties = new LdioHttpSparqlOutProperties(componentProperties);

		assertThat(httpSparqlOutProperties.getSkolemisationDomain()).contains("http://example.org");
	}

	@Test
	void test_CustomGraph() {
		final Map<String, String> properties = Map.of(GRAPH, "http://example.org");
		final ComponentProperties componentProperties = new ComponentProperties("test-pipeline", "cname", properties);

		final LdioHttpSparqlOutProperties httpSparqlOutProperties = new LdioHttpSparqlOutProperties(componentProperties);

		assertThat(httpSparqlOutProperties.getGraph()).contains("http://example.org");
	}

	@Nested
	class Replacement {
		@Test
		void test_CustomReplacementDepth() {
			final Map<String, String> properties = Map.of(REPLACEMENT_DEPTH, "15");
			final ComponentProperties componentProperties = new ComponentProperties("test-pipeline", "cname", properties);

			final LdioHttpSparqlOutProperties httpSparqlOutProperties = new LdioHttpSparqlOutProperties(componentProperties);

			assertThat(httpSparqlOutProperties)
					.is(replacementEnabled())
					.has(replacementDepth(15))
					.has(noReplacementDeleteFunction());
		}

		@Test
		void test_DisabledReplacement() {
			final Map<String, String> properties = Map.of(REPLACEMENT_ENABLED, Boolean.FALSE.toString());
			final ComponentProperties componentProperties = new ComponentProperties("test-pipeline", "cname", properties);

			final LdioHttpSparqlOutProperties httpSparqlOutProperties = new LdioHttpSparqlOutProperties(componentProperties);

			assertThat(httpSparqlOutProperties).is(not(replacementEnabled()));
		}

		@Test
		void test_DeleteFunction() {
			final String deleteFunction = "DELETE *";
			final Map<String, String> properties = Map.of(REPLACEMENT_DELETE_FUNCTION, deleteFunction);
			final ComponentProperties componentProperties = new ComponentProperties("test-pipeline", "cname", properties);

			final LdioHttpSparqlOutProperties httpSparqlOutProperties = new LdioHttpSparqlOutProperties(componentProperties);

			assertThat(httpSparqlOutProperties).is(replacementEnabled());
			assertThat(httpSparqlOutProperties.getReplacementDeleteFunction()).contains(deleteFunction);
		}
	}

	private Condition<LdioHttpSparqlOutProperties> emptyProperty(Function<LdioHttpSparqlOutProperties, Optional<String>> extractor) {
		return new Condition<>(properties -> extractor.apply(properties).isEmpty(), "Expected property to be empty");
	}

	private Condition<LdioHttpSparqlOutProperties> replacementEnabled() {
		return new Condition<>(LdioHttpSparqlOutProperties::isReplacementEnabled, "replacement.enabled expected to be true");
	}

	private Condition<LdioHttpSparqlOutProperties> replacementDepth(int depth) {
		return new Condition<>(properties -> properties.getReplacementDepth() == depth, "replacement.depth expected to be %d", depth);
	}

	private Condition<LdioHttpSparqlOutProperties> noReplacementDeleteFunction() {
		return new Condition<>(properties -> properties.getReplacementDeleteFunction().isEmpty(), "replacement.deleteFunction expected to be empty");
	}
}