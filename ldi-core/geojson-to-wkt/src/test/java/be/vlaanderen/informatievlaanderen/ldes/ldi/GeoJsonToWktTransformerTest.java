package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;
import static org.junit.jupiter.api.Assertions.*;

class GeoJsonToWktTransformerTest {

	private final GeoJsonToWktTransformer transformer = new GeoJsonToWktTransformer();

	@Test
	void testApply() {
		Model result = transformer.transform(
				RDFParser.source("geojson-all-types.json").lang(Lang.JSONLD).build().toModel());

		Model expectedResult = RDFParser.source("result-all-types.json").lang(Lang.JSONLD).build().toModel();

		assertTrue(expectedResult.isIsomorphicWith(result));
	}

	@Test
	void shouldThrowException_whenGeometryContainsMultipleTypes() {
		Model model = RDFParser.source("geojson-point.json").lang(Lang.JSONLD).build().toModel();
		Property pointProperty = createProperty("https://purl.org/geojson/vocab#Point");
		Resource subject = model.listSubjectsWithProperty(RDF.type, pointProperty).next();
		Property lineStringProperty = createProperty("https://purl.org/geojson/vocab#LineString");
		model.add(createStatement(subject, RDF.type, lineStringProperty));

		var exception = assertThrows(IllegalArgumentException.class, () -> transformer.transform(model));

		assertEquals("Could not determine http://www.w3.org/1999/02/22-rdf-syntax-ns#type of " +
				"https://purl.org/geojson/vocab#geometry", exception.getMessage());
	}

}
