package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;
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

	private final static Model initModel = ModelFactory.createDefaultModel();

	private final static String constructQuery = """	
			CONSTRUCT {
			  <http://transformed-quad/> <http://test/> "Transformed data"
			}
			WHERE { ?s ?p ?o }
			""";

	private final static String geoConstructQuery = """
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

	private final Model geoModel = RDFParser.fromString("""
						<subject> <description> "A Linestring" .
			<subject> <http://www.opengis.net/ont/geosparql#asWKT> "LINESTRING (3.5499566360323342 50.8944627132135, 3.928202753880612 50.677574117590524, 3.637920849485539 50.45967858693999)"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .
			""").lang(Lang.NQUADS).toModel();

	private final Model geoModel2 = RDFParser.fromString("""
						<subject> <description> "A Linestring" .
			<subject> <http://www.opengis.net/ont/geosparql#asWKT> "MULTILINESTRING ((3.5499566360323342 50.8944627132135, 3.928202753880612 50.677574117590524), (3.928202753880612 50.677574117590524, 3.637920849485539 50.45967858693999)"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .
			""").lang(Lang.NQUADS).toModel();

	private final Statement originalData = initModel.createStatement(
			initModel.createResource("http://data-from-source/"),
			initModel.createProperty("http://test/"),
			"Source data!");

	private final Statement transformedData = initModel.createStatement(
			initModel.createResource("http://transformed-quad/"),
			initModel.createProperty("http://test/"),
			"Transformed data");

	@Test
	void when_executeTransform_ExpectTransformedModel() {
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(constructQuery), false);

		Model model = ModelFactory.createDefaultModel().add(originalData);

		List<Model> models = sparqlConstructTransformer.apply(model);

		assertTrue(models.get(0).contains(transformedData));
		assertFalse(models.get(0).contains(originalData));

	}

	@Test
	void when_executeTransform_includeOriginal_ExpectTransformedModelWithOriginal() {
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(constructQuery), true);

		Model model = ModelFactory.createDefaultModel().add(originalData);

		List<Model> models = sparqlConstructTransformer.apply(model);

		assertTrue(models.get(0).contains(transformedData));
		assertTrue(models.get(0).contains(originalData));
	}

	@Test
	void shouldSplitModels_whenQueryContainsGraph_AndHasNoDefaultModelStatements() throws IOException {
		Model inputModel = RDFParser.source("crowdscan/input.ttl").toModel();
		String constructQuery = Files.readString(Path.of("./src/test/resources/crowdscan/query.rq"));
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(constructQuery), false);

		List<Model> result = sparqlConstructTransformer.apply(inputModel);

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

		List<Model> result = sparqlConstructTransformer.apply(inputModel);

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

		List<Model> result = sparqlConstructTransformer.apply(inputModel);

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

	@Test
	void geo_test() {
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(geoConstructQuery), false);

		List<Model> models = sparqlConstructTransformer.apply(geoModel2);

		assertEquals(1, models.get(0).listStatements().toList().size());
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

}
