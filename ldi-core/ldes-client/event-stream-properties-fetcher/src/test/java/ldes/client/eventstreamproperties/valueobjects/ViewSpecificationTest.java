package ldes.client.eventstreamproperties.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ViewSpecificationTest {
	@ParameterizedTest
	@ValueSource(strings = {"models/view.ttl", "models/eventstream.ttl"})
	void test_ExtractEventStreamProperties(String fileUri) {
		final EventStreamProperties expectedESProperties = new EventStreamProperties(
				"http://localhost:12121/observations",
				"http://purl.org/dc/terms/isVersionOf",
				"http://www.w3.org/ns/prov#generatedAtTime",
				""
		);
		final Model model = RDFParser.source(fileUri).toModel();

		final EventStreamProperties eventStreamProperties = new ViewSpecification(model).extractEventStreamProperties();

		assertThat(eventStreamProperties)
				.usingRecursiveComparison()
				.isEqualTo(expectedESProperties);
	}
}