package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoJsonToWktTransformerTest {

	private final GeoJsonToWktTransformer transformer = new GeoJsonToWktTransformer();

	// TODO: 23/03/2023 add geometry collection
	@Test
	void testApply() {
		Model result = transformer.apply(
				RDFParser.source("geojson-all-types.json").lang(Lang.JSONLD).build().toModel()
		);

		Model expectedResult =
				RDFParser.source("result-all-types.json").lang(Lang.JSONLD).build().toModel();

		assertTrue(expectedResult.isIsomorphicWith(result));
	}

}