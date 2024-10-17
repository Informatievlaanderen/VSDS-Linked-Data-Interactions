package ldes.client.eventstreamproperties.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TreeNodeSpecificationTest {
	@Test
	void test_ExtractEventStreamProperties() {
		final EventStreamProperties expectedESProperties = new EventStreamProperties("http://localhost:12121/observations");
		final Model model = RDFParser.source("models/treenode.ttl").toModel();

		final EventStreamProperties eventStreamProperties = new TreeNodeSpecification(model).extractEventStreamProperties();

		assertThat(eventStreamProperties)
				.usingRecursiveComparison()
				.isEqualTo(expectedESProperties);
	}
}