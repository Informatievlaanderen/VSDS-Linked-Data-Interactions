package be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlfunctions;

import be.vlaanderen.informatievlaanderen.ldes.ldi.SparqlConstructTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.datasetsplitter.DatasetSplitters;
import be.vlaanderen.informatievlaanderen.ldes.ldi.utils.SparqlFunctionsUtils;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.geosparql.implementation.vocabulary.Geo;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.*;
import org.assertj.core.api.Condition;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SparqlConstructFunctionsTest {

	private static final String TELRAAM_CSV = "/geo-functions/telraam.csv";
	private static final String DATA_FROM_SOURCE = "http://data-from-source/";
	private static final Resource DATA_FROM_SOURCE_RES = ResourceFactory.createResource(DATA_FROM_SOURCE);
	private static final String GEOSPARQL_CUSTOM = "https://opengis.net/def/function/geosparql/custom#";
	private static final String EXAMPLE_ORG = "http://example.org/";
	private static final String GEOMETRY = "geometry";
	private static final String POINT = "point";
	private static final String INDEX = "index";
	private static final String OFFSET = "offset";
	private static final WKTDatatype WKT_DATATYPE = WKTDatatype.INSTANCE;
	private SparqlConstructTransformer sparqlConstructTransformer;

	@ParameterizedTest
	@CsvFileSource(resources = TELRAAM_CSV, numLinesToSkip = 1)
	void geoFunctions_firstCoordinate(String id, String midpoint, String line, String start) {
		initSparqlConstructTransformer("geo-construct-first-coordinate.rq");

		List<Model> result = sparqlConstructTransformer.transform(createGeoModel(line));

		assertThat(result).are(containing(DATA_FROM_SOURCE_RES, Geo.AS_WKT_PROP, WKT_DATATYPE.read(start).asLiteral()));
	}

	@ParameterizedTest
	@CsvFileSource(resources = TELRAAM_CSV, numLinesToSkip = 1)
	void geoFunctions_lastCoordinate(String id, String midpoint, String line, String start, String end) {
		initSparqlConstructTransformer("geo-construct-last-coordinate.rq");

		List<Model> result = sparqlConstructTransformer.transform(createGeoModel(line));

		assertThat(result).are(containing(DATA_FROM_SOURCE_RES, Geo.AS_WKT_PROP, WKT_DATATYPE.read(end).asLiteral()));
	}

	@ParameterizedTest
	@CsvFileSource(resources = TELRAAM_CSV, numLinesToSkip = 1)
	void geoFunctions_lineLength(String id, String midpoint, String line, String start, String end, String offset) {
		initSparqlConstructTransformer("geo-construct-line-length.rq");
		double expected = (double) Math.round((Double.parseDouble(offset) * 2) * 100) / 100;

		List<Model> result = sparqlConstructTransformer.transform(createGeoModel(line));

		assertThat(result)
				.map(model -> model.listObjectsOfProperty(ResourceFactory.createProperty("http://www.opengis.net/ont/geosparql#lineLength")).nextNode().asLiteral().getDouble())
				.allSatisfy(distanceFromStart -> assertThat(distanceFromStart).isEqualTo(expected, Offset.offset(0.02)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = TELRAAM_CSV, numLinesToSkip = 1)
	void geoFunctions_midPoint(String id, String midpoint, String line) {
		initSparqlConstructTransformer("geo-construct-mid-point.rq");

		List<Model> result = sparqlConstructTransformer.transform(createGeoModel(line));

		assertThat(result).are(containingCoordinateLiteral(midpoint));
	}

	@ParameterizedTest
	@CsvFileSource(resources = TELRAAM_CSV, numLinesToSkip = 1)
	void geoFunctions_pointAtFromStart(String id, String midpoint, String line, String start, String end, String offset) {
		initSparqlConstructTransformer("geo-construct-point-at-from-start.rq");

		List<Model> result = sparqlConstructTransformer.transform(createGeoModel(line, offset));

		assertThat(result).are(containingCoordinateLiteral(midpoint));
	}

	@ParameterizedTest
	@CsvFileSource(resources = TELRAAM_CSV, numLinesToSkip = 1)
	void geoFunctions_distanceFromStart(String id, String midpoint, String line, String start, String end, String offset) {
		initSparqlConstructTransformer("geo-construct-distance-from-start.rq");

		List<Model> result = sparqlConstructTransformer.transform(createGeoModelTwoLiterals(line, midpoint));

		assertThat(result)
				.map(model -> model.listStatements().nextStatement().getDouble())
				.allSatisfy(distanceFromStart -> assertThat(distanceFromStart).isEqualTo(Double.parseDouble(offset), Offset.offset(0.02)));
	}

	@Test
	void geoFunctions_getLineAtIndex() {
		initSparqlConstructTransformer("geo-construct-line-at-index.rq");
		String multiLineString = "MULTILINESTRING ((10 10, 20 20, 10 40), (40 40, 30 30, 40 20, 30 10))";
		String lineString = "LINESTRING(10 10, 20 20, 10 40)";
		int index = 0;

		List<Model> result = sparqlConstructTransformer.transform(createGeoModel(multiLineString, index));

		assertThat(result).are(containing(DATA_FROM_SOURCE_RES, Geo.AS_WKT_PROP, WKT_DATATYPE.read(lineString).asLiteral()));
	}

	private void initSparqlConstructTransformer(String simpleFilename) {
		final Query query = QueryFactory.read("geo-functions/queries/%s".formatted(simpleFilename));
		sparqlConstructTransformer = new SparqlConstructTransformer(query, false, DatasetSplitters.splitByNamedGraph());
	}

	private Model createGeoModel(String wkt) {
		return ModelFactory.createDefaultModel().add(DATA_FROM_SOURCE_RES, Geo.AS_WKT_PROP, WKT_DATATYPE.read(wkt).asLiteral());
	}

	private Model createGeoModel(String wkt, String offset) {
		return createGeoModel(wkt)
				.add(DATA_FROM_SOURCE_RES, ResourceFactory.createProperty(GEOSPARQL_CUSTOM, OFFSET), ResourceFactory.createPlainLiteral(offset));
	}

	private Model createGeoModel(String wkt, int index) {
		return createGeoModel(wkt).add(
				DATA_FROM_SOURCE_RES,
				ResourceFactory.createProperty(GEOSPARQL_CUSTOM, INDEX),
				ResourceFactory.createTypedLiteral(String.valueOf(index), XSDDatatype.XSDint));
	}

	private Model createGeoModelTwoLiterals(String wkt, String point) {
		return ModelFactory.createDefaultModel()
				.add(DATA_FROM_SOURCE_RES, ResourceFactory.createProperty(EXAMPLE_ORG, GEOMETRY), WKT_DATATYPE.read(wkt).asLiteral())
				.add(DATA_FROM_SOURCE_RES, ResourceFactory.createProperty(EXAMPLE_ORG, POINT), WKT_DATATYPE.read(point).asLiteral());
	}

	private Condition<Model> containing(Resource subject, Property predicate, RDFNode object) {
		return new Condition<>(model -> model.contains(subject, predicate, object), "containing %s %s %s", subject, predicate, object);
	}

	private Condition<Model> containingCoordinateLiteral(String expectedAsString) {
		Coordinate expected = SparqlFunctionsUtils.getCoordinatesFromPointAsString(expectedAsString);
		return new Condition<>(actual -> extractCoordinateFromModel(actual).equals2D(expected, 0.00004), "equal to %s", expected);
	}

	private Coordinate extractCoordinateFromModel(Model model) {
		return GeometryWrapper.extract(model.listStatements().nextStatement().getObject().asNode()).getXYGeometry().getCoordinates()[0];
	}
}