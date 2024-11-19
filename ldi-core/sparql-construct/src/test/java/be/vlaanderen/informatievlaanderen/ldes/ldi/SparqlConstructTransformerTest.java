package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlfunctions.*;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SparqlConstructTransformerTest {

	private static final Model INIT_MODEL = ModelFactory.createDefaultModel();

	private static final String DEFAULT_CONSTRUCT_QUERY = """
			CONSTRUCT {
			  <http://transformed-quad/> <http://test/> "Transformed data"
			}
			WHERE { ?s ?p ?o }
			""";

	private static final String GEO_CONSTRUCT_FIRST_COORDINATE_QUERY = """
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

	private final Statement originalData = INIT_MODEL.createStatement(
			INIT_MODEL.createResource("http://data-from-source/"),
			INIT_MODEL.createProperty("http://test/"),
			"Source data!");

	private final Statement transformedData = INIT_MODEL.createStatement(
			INIT_MODEL.createResource("http://transformed-quad/"),
			INIT_MODEL.createProperty("http://test/"),
			"Transformed data");

	@Test
	void when_executeTransform_ExpectTransformedModel() {
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(DEFAULT_CONSTRUCT_QUERY), false);

		Model model = ModelFactory.createDefaultModel().add(originalData);

		List<Model> models = sparqlConstructTransformer.transform(model);

		assertTrue(models.get(0).contains(transformedData));
		assertFalse(models.get(0).contains(originalData));

	}

	@Test
	void when_executeTransform_includeOriginal_ExpectTransformedModelWithOriginal() {
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(DEFAULT_CONSTRUCT_QUERY), true);

		Model model = ModelFactory.createDefaultModel().add(originalData);

		List<Model> models = sparqlConstructTransformer.transform(model);

		assertTrue(models.get(0).contains(transformedData));
		assertTrue(models.get(0).contains(originalData));
	}

	@Test
	void shouldSplitModels_whenQueryContainsGraph_AndHasNoDefaultModelStatements() throws IOException {
		Model inputModel = RDFParser.source("crowdscan/input.ttl").toModel();
		String constructQuery = Files.readString(Path.of("./src/test/resources/crowdscan/query.rq"));
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(constructQuery), false);

		List<Model> result = sparqlConstructTransformer.transform(inputModel);

		assertEquals(3, result.size());
		assertModels(
				List.of("crowdscan/observation1.ttl", "crowdscan/observation2.ttl", "crowdscan/observation3.ttl"),
				result);
	}

	@Test
	void shouldNotSplitModels_whenQueryContainsNoGraph_AndHasOnlyDefaultModelStatements() throws IOException {
		Model inputModel = RDFParser.source("crowdscan/input.ttl").toModel();
		String constructQuery = Files.readString(Path.of("./src/test/resources/crowdscan/no-graph-query.rq"));
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(constructQuery), false);

		List<Model> result = sparqlConstructTransformer.transform(inputModel);

		assertEquals(1, result.size());
		Model expectedResult = RDFParser.source("crowdscan/no-graph-observations.ttl").toModel();
		assertTrue(expectedResult.isIsomorphicWith(result.get(0)));
	}

	@Test
	void shouldSplitModels_whenQueryContainsGraph_AndHasDefaultModelStatements() throws IOException {
		Model inputModel = RDFParser.source("traffic/input.ttl").toModel();
		String constructQuery = Files.readString(Path.of("./src/test/resources/traffic/query.rq"));
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(constructQuery), false);

		List<Model> result = sparqlConstructTransformer.transform(inputModel);

		assertEquals(10, result.size());
		assertModels(List.of(
				"traffic/measure1.ttl",
				"traffic/measure2.ttl",
				"traffic/measure3.ttl",
				"traffic/measure4.ttl",
				"traffic/measure5.ttl",
				"traffic/measure6.ttl",
				"traffic/measure7.ttl",
				"traffic/measure8.ttl",
				"traffic/measure9.ttl",
				"traffic/measure10.ttl"), result);
	}

	private void assertModels(List<String> expectedModelPaths, Collection<Model> result) {
		Set<Model> expectedModels = expectedModelPaths
				.stream()
				.map(RDFParser::source)
				.map(RDFParserBuilder::toModel)
				.collect(Collectors.toSet());

		result.forEach(
				actualResult -> {
					System.out.println(RDFWriter.source(actualResult).lang(Lang.TURTLE).asString());
					expectedModels
							.removeIf(expectedResult -> expectedResult.isIsomorphicWith(actualResult));
				});

		assertTrue(expectedModels.isEmpty());
	}

	@Test
	void initGeoFunctionsTest() {
		new SparqlConstructTransformer(QueryFactory.create(GEO_CONSTRUCT_FIRST_COORDINATE_QUERY), false);

		FunctionRegistry registry = FunctionRegistry.get();

		assertTrue(registry.isRegistered(FirstCoordinate.NAME));
		assertTrue(registry.isRegistered(LastCoordinate.NAME));
		assertTrue(registry.isRegistered(LineLength.NAME));
		assertTrue(registry.isRegistered(MidPoint.NAME));
		assertTrue(registry.isRegistered(PointAtFromStart.NAME));
		assertTrue(registry.isRegistered(DistanceFromStart.NAME));
		assertTrue(registry.isRegistered(LineAtIndex.NAME));
	}
}