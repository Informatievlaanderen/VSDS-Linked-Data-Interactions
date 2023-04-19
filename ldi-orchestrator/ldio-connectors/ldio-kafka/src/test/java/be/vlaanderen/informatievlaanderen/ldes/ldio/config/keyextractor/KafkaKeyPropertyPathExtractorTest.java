package be.vlaanderen.informatievlaanderen.ldes.ldio.config.keyextractor;

import be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor.KafkaKeyPropertyPathExtractor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class KafkaKeyPropertyPathExtractorTest {

	@ParameterizedTest
	@ArgumentsSource(PropertyPathExtractorProvider.class)
	void testGetKey(String testName, String input, String propertyPath, String result) {
		assertNotNull(testName);
		Model model = RDFParser.fromString(input).lang(Lang.NQUADS).build().toModel();
		KafkaKeyPropertyPathExtractor extractor = new KafkaKeyPropertyPathExtractor(propertyPath);
		assertEquals(result, extractor.getKey(model));
	}

	static class PropertyPathExtractorProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(
							"shouldReturnLiteralStringWhenLinkedPath",
							"""
										<https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> <https://example.com/hindrances/zones/a> .
										<https://example.com/hindrances/zones/a> <https://data.com/ns/mobiliteit#Zone.type> 'my-zone-type' .
									""",
							"<https://data.com/ns/mobiliteit#zone>/<https://data.com/ns/mobiliteit#Zone.type>",
							"my-zone-type"),
					Arguments.of(
							"shouldReturnAnyResultStringWhenMultipleResults",
							"""
										<https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> <https://example.com/hindrances/zones/a> .
										<https://example.com/hindrances/zones/a> <https://data.com/ns/mobiliteit#Zone.type> 'my-zone-type' .
									""",
							"<https://data.com/ns/mobiliteit#zone>/<https://data.com/ns/mobiliteit#Zone.type>",
							"my-zone-type"),
					Arguments.of(
							"shouldReturnUriAsStringWhenObjectIsResource",
							"<https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> <https://example.com/hindrances/zones/a> .",
							"<https://data.com/ns/mobiliteit#zone>",
							"https://example.com/hindrances/zones/a"),
					Arguments.of(
							"shouldReturnNullIfPathIsNotFound",
							"<https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> <https://example.com/hindrances/zones/a> .",
							"<https://not-existing>",
							null),
					Arguments.of(
							"shouldReturnLiteralStringWhenSimplePath",
							"<https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> 'my-zone-type' .",
							"<https://data.com/ns/mobiliteit#zone>",
							"my-zone-type"));
		}
	}

}