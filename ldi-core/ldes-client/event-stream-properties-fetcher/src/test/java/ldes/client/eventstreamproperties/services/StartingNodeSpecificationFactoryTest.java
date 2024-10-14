package ldes.client.eventstreamproperties.services;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StartingNodeSpecificationFactoryTest {
	@Test
	void given_invalidModel_when_FromModel_then_ThrowException() {
		String invalid = "<http://localhost:12121/observations/by-page?pageNumber=1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/dc/terms/Standard> .";
		Model model = RDFParser.fromString(invalid).lang(Lang.NQ).toModel();

		assertThatThrownBy(() -> StartingNodeSpecificationFactory.fromModel(model))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("The model is not a valid StartingNodeSpecification");
	}
}