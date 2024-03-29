package be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlfunctions;

import be.vlaanderen.informatievlaanderen.ldes.ldi.SparqlConstructTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.utils.SparqlFunctionsUtils;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.vocabulary.GeoSPARQL_URI;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SparqlConstructFunctionsTest {

	private final static Model INIT_MODEL = ModelFactory.createDefaultModel();
	public static final String TELRAAM_CSV = "/geo-functions/telraam.csv";
	public static final String DATA_FROM_SOURCE = "http://data-from-source/";
	public static final String GEOSPARQL_AS_WKT = "http://www.opengis.net/ont/geosparql#asWKT";
	public static final String WKT_LITERAL = "wktLiteral";
	public static final String GEOSPARQL_CUSTOM = "https://opengis.net/def/function/geosparql/custom#";
	public static final String EXAMPLE_ORG = "http://example.org/";
	public static final String GEOMETRY = "geometry";
	public static final String POINT = "point";
	public static final String INDEX = "index";
	public static final String OFFSET = "offset";

	private final static String geoConstructFirstCoordinateQuery = """
			prefix geoc: <https://opengis.net/def/function/geosparql/custom#>
			prefix geosparql: <http://www.opengis.net/ont/geosparql#>

			CONSTRUCT  {
				?s geosparql:asWKT ?value
			}
			WHERE {
				?s geosparql:asWKT ?wkt
				BIND (geoc:firstCoordinate(?wkt) as ?value)
			}

			""";

	private final static String geoConstructLastCoordinateQuery = """
			prefix geoc: <https://opengis.net/def/function/geosparql/custom#>
			prefix geosparql: <http://www.opengis.net/ont/geosparql#>

			CONSTRUCT  {
				?s geosparql:asWKT ?value
			}
			WHERE {
				?s geosparql:asWKT ?wkt
				BIND (geoc:lastCoordinate(?wkt) as ?value)
			}

			""";

	private final static String geoConstructLineLengthQuery = """
			prefix geoc: <https://opengis.net/def/function/geosparql/custom#>
			prefix geosparql: <http://www.opengis.net/ont/geosparql#>

			CONSTRUCT  {
				?s geosparql:lineLength ?value
			}
			WHERE {
				?s geosparql:asWKT ?wkt
				BIND (geoc:lineLength(?wkt) as ?value)
			}

			""";

	private final static String geoConstructMidPointQuery = """
			prefix geoc: <https://opengis.net/def/function/geosparql/custom#>
			prefix geosparql: <http://www.opengis.net/ont/geosparql#>

			CONSTRUCT  {
				?s geosparql:asWKT ?value
			}
			WHERE {
				?s geosparql:asWKT ?wkt
				BIND (geoc:midPoint(?wkt) as ?value)
			}

			""";

	private final static String geoConstructPointAtFromStartQuery = """
			prefix geoc: <https://opengis.net/def/function/geosparql/custom#>
			prefix geosparql: <http://www.opengis.net/ont/geosparql#>

			CONSTRUCT  {
				?s geosparql:asWKT ?value
			}
			WHERE {
				?s geosparql:asWKT ?wkt .
				?s geoc:offset ?ofs .
				BIND (geoc:pointAtFromStart(?wkt, ?ofs) as ?value)
			}

			""";

	private final static String geoConstructDistanceFromStartQuery = """
			prefix geoc: <https://opengis.net/def/function/geosparql/custom#>
			prefix geosparql: <http://www.opengis.net/ont/geosparql#>
			prefix : <http://example.org/>

			CONSTRUCT  {
				?s geoc:distanceFromStart ?value
			}
			WHERE {
				?s :geometry ?wkt .
			    ?s :point ?pnt .
				BIND (geoc:distanceFromStart(?wkt, ?pnt) as ?value)
			}

			""";

	private final static String geoConstructLineAtIndexQuery = """
			prefix geoc: <https://opengis.net/def/function/geosparql/custom#>
			prefix geosparql: <http://www.opengis.net/ont/geosparql#>

			CONSTRUCT  {
				?s geosparql:asWKT ?value
			}
			WHERE {
				?s geosparql:asWKT ?wkt .
				?s geoc:index ?idx .
				BIND (geoc:lineAtIndex(?wkt, ?idx) as ?value)
			}

			""";

	@ParameterizedTest
	@CsvFileSource(resources = TELRAAM_CSV, numLinesToSkip = 1)
	void geoFunctions_firstCoordinate(String id, String midpoint, String line, String start) {
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(geoConstructFirstCoordinateQuery), false);

		List<Model> result = sparqlConstructTransformer.transform(createGeoModel(line));

		Statement expected = createStatement(INIT_MODEL.createResource(DATA_FROM_SOURCE),
				INIT_MODEL.createProperty(GEOSPARQL_AS_WKT),
				INIT_MODEL.createTypedLiteral(start, GeoSPARQL_URI.GEO_URI + WKT_LITERAL));

		assertTrue(result.get(0).contains(expected));
	}

	@ParameterizedTest
	@CsvFileSource(resources = TELRAAM_CSV, numLinesToSkip = 1)
	void geoFunctions_lastCoordinate(String id, String midpoint, String line, String start, String end) {
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(geoConstructLastCoordinateQuery), false);

		List<Model> result = sparqlConstructTransformer.transform(createGeoModel(line));

		Statement expected = createStatement(INIT_MODEL.createResource(DATA_FROM_SOURCE),
				INIT_MODEL.createProperty(GEOSPARQL_AS_WKT),
				INIT_MODEL.createTypedLiteral(end, GeoSPARQL_URI.GEO_URI + WKT_LITERAL));

		assertTrue(result.get(0).contains(expected));
	}

	@ParameterizedTest
	@CsvFileSource(resources = TELRAAM_CSV, numLinesToSkip = 1)
	void geoFunctions_lineLength(String id, String midpoint, String line, String start, String end, String offset) {
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(geoConstructLineLengthQuery), false);

		List<Model> result = sparqlConstructTransformer.transform(createGeoModel(line));

		double expected = (double) Math.round((Double.parseDouble(offset) * 2) * 100) / 100;

		double lineLength = result.get(0)
				.listObjectsOfProperty(createProperty("http://www.opengis.net/ont/geosparql#lineLength"))
				.toList().get(0).asLiteral().getDouble();

		assertEquals(lineLength, expected, 0.02);
	}

	@ParameterizedTest
	@CsvFileSource(resources = TELRAAM_CSV, numLinesToSkip = 1)
	void geoFunctions_midPoint(String id, String midpoint, String line) {
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(geoConstructMidPointQuery), false);

		List<Model> result = sparqlConstructTransformer.transform(createGeoModel(line));

		Coordinate expected = SparqlFunctionsUtils.getCoordinatesFromPointAsString(midpoint);

		Coordinate calculated = GeometryWrapper
				.extract(result.get(0).listStatements().nextStatement().getObject().asNode()).getXYGeometry()
				.getCoordinates()[0];

		assertTrue(calculated.equals2D(expected, 0.00004));
	}

	@ParameterizedTest
	@CsvFileSource(resources = TELRAAM_CSV, numLinesToSkip = 1)
	void geoFunctions_pointAtFromStart(String id, String midpoint, String line, String start, String end,
			String offset) {
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(geoConstructPointAtFromStartQuery), false);

		List<Model> result = sparqlConstructTransformer.transform(createGeoModel(line, offset));

		Coordinate expected = SparqlFunctionsUtils.getCoordinatesFromPointAsString(midpoint);

		Coordinate calculated = GeometryWrapper
				.extract(result.get(0).listStatements().nextStatement().getObject().asNode()).getXYGeometry()
				.getCoordinates()[0];

		assertTrue(calculated.equals2D(expected, 0.00004));
	}

	@ParameterizedTest
	@CsvFileSource(resources = TELRAAM_CSV, numLinesToSkip = 1)
	void geoFunctions_distanceFromStart(String id, String midpoint, String line, String start, String end,
			String offset) {

		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(geoConstructDistanceFromStartQuery), false);

		List<Model> result = sparqlConstructTransformer.transform(createGeoModelTwoLiterals(line, midpoint));

		double expected = Double.parseDouble(offset);
		double distanceFromStart = result.get(0).listStatements().nextStatement().getDouble();

		assertEquals(expected, distanceFromStart, 0.02);
	}

	@Test
	void geoFunctions_getLineAtIndex() {
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(geoConstructLineAtIndexQuery), false);

		String multiLineString = "MULTILINESTRING ((10 10, 20 20, 10 40), (40 40, 30 30, 40 20, 30 10))";
		int index = 0;
		List<Model> result = sparqlConstructTransformer.transform(createGeoModel(multiLineString, index));

		String lineString = "LINESTRING(10 10, 20 20, 10 40)";
		Statement expected = createStatement(INIT_MODEL.createResource(DATA_FROM_SOURCE),
				INIT_MODEL.createProperty(GEOSPARQL_AS_WKT),
				INIT_MODEL.createTypedLiteral(lineString, GeoSPARQL_URI.GEO_URI + WKT_LITERAL));

		assertTrue(result.get(0).contains(expected));
	}

	private Model createGeoModel(String wkt) {
		Statement geoStatement = INIT_MODEL.createStatement(
				INIT_MODEL.createResource(DATA_FROM_SOURCE),
				INIT_MODEL.createProperty(GEOSPARQL_AS_WKT),
				INIT_MODEL.createTypedLiteral(wkt, GeoSPARQL_URI.GEO_URI + WKT_LITERAL));
		return ModelFactory.createDefaultModel().add(geoStatement);
	}

	private Model createGeoModel(String wkt, String offset) {
		Statement geoStatement = INIT_MODEL.createStatement(
				INIT_MODEL.createResource(DATA_FROM_SOURCE),
				INIT_MODEL.createProperty(GEOSPARQL_AS_WKT),
				INIT_MODEL.createTypedLiteral(wkt, GeoSPARQL_URI.GEO_URI + WKT_LITERAL));

		Statement geoStatement2 = INIT_MODEL.createStatement(
				INIT_MODEL.createResource(DATA_FROM_SOURCE),
				INIT_MODEL.createProperty(GEOSPARQL_CUSTOM + OFFSET),
				INIT_MODEL.createLiteral(offset));

		return ModelFactory.createDefaultModel().add(geoStatement).add(geoStatement2);
	}

	private Model createGeoModel(String wkt, int index) {
		Statement geoStatement = INIT_MODEL.createStatement(
				INIT_MODEL.createResource(DATA_FROM_SOURCE),
				INIT_MODEL.createProperty(GEOSPARQL_AS_WKT),
				INIT_MODEL.createTypedLiteral(wkt, GeoSPARQL_URI.GEO_URI + WKT_LITERAL));

		Statement geoStatement2 = INIT_MODEL.createStatement(
				INIT_MODEL.createResource(DATA_FROM_SOURCE),
				INIT_MODEL.createProperty(GEOSPARQL_CUSTOM + INDEX),
				INIT_MODEL.createTypedLiteral(index, XSDDatatype.XSDint));

		return ModelFactory.createDefaultModel().add(geoStatement).add(geoStatement2);
	}

	private Model createGeoModelTwoLiterals(String wkt, String point) {
		Statement geoStatement = INIT_MODEL.createStatement(
				INIT_MODEL.createResource(DATA_FROM_SOURCE),
				INIT_MODEL.createProperty(EXAMPLE_ORG + GEOMETRY),
				INIT_MODEL.createTypedLiteral(wkt, GeoSPARQL_URI.GEO_URI + WKT_LITERAL));

		Statement geoStatement2 = INIT_MODEL.createStatement(
				INIT_MODEL.createResource(DATA_FROM_SOURCE),
				INIT_MODEL.createProperty(EXAMPLE_ORG + POINT),
				INIT_MODEL.createTypedLiteral(point, GeoSPARQL_URI.GEO_URI + WKT_LITERAL));

		return ModelFactory.createDefaultModel().add(geoStatement).add(geoStatement2);
	}
}