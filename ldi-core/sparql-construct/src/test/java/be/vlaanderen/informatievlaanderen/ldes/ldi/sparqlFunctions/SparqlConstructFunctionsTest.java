package be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlFunctions;

import be.vlaanderen.informatievlaanderen.ldes.ldi.SparqlConstructTransformer;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.vocabulary.GeoSPARQL_URI;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SparqlConstructFunctionsTest {

	private final static Model initModel = ModelFactory.createDefaultModel();

	private final Model createGeoModel(String wkt) {
		Statement geoStatement = initModel.createStatement(
				initModel.createResource("http://data-from-source/"),
				initModel.createProperty("http://www.opengis.net/ont/geosparql#asWKT"),
				initModel.createTypedLiteral(wkt, GeoSPARQL_URI.GEO_URI + "wktLiteral"));
		return ModelFactory.createDefaultModel().add(geoStatement);
	}

	private final Model createGeoModelTwo(String wkt, String offset) {
		Statement geoStatement = initModel.createStatement(
				initModel.createResource("http://data-from-source/"),
				initModel.createProperty("http://www.opengis.net/ont/geosparql#asWKT"),
				initModel.createTypedLiteral(wkt, GeoSPARQL_URI.GEO_URI + "wktLiteral"));

		Statement geoStatement2 = initModel.createStatement(
				initModel.createResource("http://data-from-source/"),
				initModel.createProperty("https://w3id.org/tree#offset"),
				initModel.createLiteral(offset));

		return ModelFactory.createDefaultModel().add(geoStatement).add(geoStatement2);
	}

	private final static String geoConstructFirstCoordinateQuery = """
			prefix tree: <https://w3id.org/tree#>
			prefix geosparql: <http://www.opengis.net/ont/geosparql#>

			CONSTRUCT  {
				?s geosparql:asWKT ?value
			}
			WHERE {
				?s geosparql:asWKT ?wkt
				BIND (tree:firstCoordinate(?wkt, 0) as ?value)
			}

			""";

	private final static String geoConstructLastCoordinateQuery = """
			prefix tree: <https://w3id.org/tree#>
			prefix geosparql: <http://www.opengis.net/ont/geosparql#>

			CONSTRUCT  {
				?s geosparql:asWKT ?value
			}
			WHERE {
				?s geosparql:asWKT ?wkt
				BIND (tree:lastCoordinate(?wkt, 0) as ?value)
			}

			""";

	private final static String geoConstructLineLengthQuery = """
			prefix tree: <https://w3id.org/tree#>
			prefix geosparql: <http://www.opengis.net/ont/geosparql#>

			CONSTRUCT  {
				?s geosparql:lineLength ?value
			}
			WHERE {
				?s geosparql:asWKT ?wkt
				BIND (tree:lineLength(?wkt, 0) as ?value)
			}

			""";

	private final static String geoConstructMidPointQuery = """
			prefix tree: <https://w3id.org/tree#>
			prefix geosparql: <http://www.opengis.net/ont/geosparql#>

			CONSTRUCT  {
				?s geosparql:asWKT ?value
			}
			WHERE {
				?s geosparql:asWKT ?wkt
				BIND (tree:midPoint(?wkt, 0) as ?value)
			}

			""";

	private final static String geoConstructPointAtFromStartQuery = """
			prefix tree: <https://w3id.org/tree#>
			prefix geosparql: <http://www.opengis.net/ont/geosparql#>

			CONSTRUCT  {
				?s geosparql:asWKT ?value
			}
			WHERE {
				?s geosparql:asWKT ?wkt .
				?s tree:offset ?ofs .
				BIND (tree:pointAtFromStart(?wkt, 0, ?ofs) as ?value)
			}

			""";

	@ParameterizedTest
	@CsvFileSource(resources = "/geo-functions/telraam.csv", numLinesToSkip = 1)
	void geoFunctions_firstCoordinate(String id, String midpoint, String line, String start, String end) {
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(geoConstructFirstCoordinateQuery), false);

		List<Model> result = sparqlConstructTransformer.apply(createGeoModel(line));

		Statement expected = createStatement(initModel.createResource("http://data-from-source/"),
				initModel.createProperty("http://www.opengis.net/ont/geosparql#asWKT"),
				initModel.createTypedLiteral(start, GeoSPARQL_URI.GEO_URI + "wktLiteral"));

		assertTrue(result.get(0).contains(expected));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/geo-functions/telraam.csv", numLinesToSkip = 1)
	void geoFunctions_lastCoordinate(String id, String midpoint, String line, String start, String end) {
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(geoConstructLastCoordinateQuery), false);

		List<Model> result = sparqlConstructTransformer.apply(createGeoModel(line));

		Statement expected = createStatement(initModel.createResource("http://data-from-source/"),
				initModel.createProperty("http://www.opengis.net/ont/geosparql#asWKT"),
				initModel.createTypedLiteral(end, GeoSPARQL_URI.GEO_URI + "wktLiteral"));

		assertTrue(result.get(0).contains(expected));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/geo-functions/telraam.csv", numLinesToSkip = 1)
	void geoFunctions_lineLength(String id, String midpoint, String line, String start, String end, String offset) {
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(geoConstructLineLengthQuery), false);

		List<Model> result = sparqlConstructTransformer.apply(createGeoModel(line));

		double expected = (double) Math.round((Double.parseDouble(offset) * 2) * 100) / 100;

		double lineLength = result.get(0)
				.listObjectsOfProperty(createProperty("http://www.opengis.net/ont/geosparql#lineLength"))
				.toList().get(0).asLiteral().getDouble();

		assertEquals(lineLength, expected, 0.02);
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/geo-functions/telraam.csv", numLinesToSkip = 1)
	void midPoint(String id, String midpoint, String line, String start, String end, String offset) {
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(geoConstructMidPointQuery), false);

		List<Model> result = sparqlConstructTransformer.apply(createGeoModel(line));

		String[] points = midpoint.replace("POINT(", "")
				.replace(")", "")
				.split(" ");

		Coordinate expected = new Coordinate(Double.parseDouble(points[0]), Double.parseDouble(points[1]));

		Coordinate calculated = GeometryWrapper
				.extract(result.get(0).listStatements().nextStatement().getObject().asNode()).getXYGeometry()
				.getCoordinates()[0];

		assertTrue(calculated.equals2D(expected, 0.00004));
	}
}