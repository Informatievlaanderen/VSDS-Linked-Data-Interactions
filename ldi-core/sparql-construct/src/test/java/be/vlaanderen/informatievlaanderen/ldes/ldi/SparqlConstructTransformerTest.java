package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.datasetsplitter.DatasetSplitter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.datasetsplitter.DatasetSplitters;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlfunctions.*;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.not;

class SparqlConstructTransformerTest {
	private static final DatasetSplitter defaultDatasetSplitter = DatasetSplitters.splitByNamedGraph();

	private static final String DEFAULT_CONSTRUCT_QUERY = """
			CONSTRUCT {
			  <http://transformed-quad/> <http://test/> "Transformed data"
			}
			WHERE { ?s ?p ?o }
			""";

	private final Statement originalData = ResourceFactory.createStatement(
			ResourceFactory.createResource("http://data-from-source/"),
			ResourceFactory.createProperty("http://test/"),
			ResourceFactory.createStringLiteral("Source data!"));

	private final Statement transformedData = ResourceFactory.createStatement(
			ResourceFactory.createResource("http://transformed-quad/"),
			ResourceFactory.createProperty("http://test/"),
			ResourceFactory.createStringLiteral("Transformed data"));

	private SparqlConstructTransformer sparqlConstructTransformer;

	@Test
	void when_executeTransform_ExpectTransformedModel() {
		sparqlConstructTransformer = new SparqlConstructTransformer(QueryFactory.create(DEFAULT_CONSTRUCT_QUERY), false, DatasetSplitters.splitByNamedGraph());
		Model model = ModelFactory.createDefaultModel().add(originalData);

		List<Model> models = sparqlConstructTransformer.transform(model);

		assertThat(models).first().is(containing(transformedData)).is(not(containing(originalData)));
	}

	@Test
	void when_executeTransform_includeOriginal_ExpectTransformedModelWithOriginal() {
		sparqlConstructTransformer = new SparqlConstructTransformer(QueryFactory.create(DEFAULT_CONSTRUCT_QUERY), true, DatasetSplitters.splitByNamedGraph());
		Model model = ModelFactory.createDefaultModel().add(originalData);

		List<Model> models = sparqlConstructTransformer.transform(model);

		assertThat(models).first().is(containing(transformedData)).is(containing(originalData));
	}

	@Test
	void shouldSplitModels_whenQueryContainsGraph_AndHasNoDefaultModelStatements() {
		Model inputModel = RDFParser.source("crowdscan/input.ttl").toModel();
		Query query = QueryFactory.read("crowdscan/query.rq");
		sparqlConstructTransformer = new SparqlConstructTransformer(query, false, DatasetSplitters.splitByNamedGraph());

		List<Model> result = sparqlConstructTransformer.transform(inputModel);

		assertThat(result)
				.hasSize(3)
				.are(containsOneModelOf("crowdscan/observation1.ttl", "crowdscan/observation2.ttl", "crowdscan/observation3.ttl"));
	}

	@Test
	void shouldNotSplitModels_whenQueryContainsNoGraph_AndHasOnlyDefaultModelStatements() {
		Model inputModel = RDFParser.source("crowdscan/input.ttl").toModel();
		Query query = QueryFactory.read("crowdscan/no-graph-query.rq");
		sparqlConstructTransformer = new SparqlConstructTransformer(query, false, DatasetSplitters.splitByNamedGraph());

		List<Model> result = sparqlConstructTransformer.transform(inputModel);

		assertThat(result)
				.hasSize(1)
				.are(isomorphicWith("crowdscan/no-graph-observations.ttl"));
	}

	@Test
	void shouldNotSplitModels_whenQueryContainsGraph_AndHasSplitByNamedGraphDisabled() {
		Model inputModel = RDFParser.source("crowdscan/input.ttl").toModel();
		Query query = QueryFactory.read("crowdscan/query.rq");
		sparqlConstructTransformer = new SparqlConstructTransformer(query, false, DatasetSplitters.preventSplitting());

		List<Model> result = sparqlConstructTransformer.transform(inputModel);

		assertThat(result)
				.hasSize(1)
				.are(isomorphicWith("crowdscan/no-graph-observations.ttl"));
	}

	@Test
	void shouldSplitModels_whenQueryContainsGraph_AndHasDefaultModelStatements() {
		String[] expectedModelPaths = {"traffic/measure1.ttl", "traffic/measure2.ttl", "traffic/measure3.ttl",
				"traffic/measure4.ttl", "traffic/measure5.ttl", "traffic/measure6.ttl", "traffic/measure7.ttl",
				"traffic/measure8.ttl", "traffic/measure9.ttl", "traffic/measure10.ttl"};
		Model inputModel = RDFParser.source("traffic/input.ttl").toModel();
		Query query = QueryFactory.read("traffic/query.rq");
		sparqlConstructTransformer = new SparqlConstructTransformer(query, false, defaultDatasetSplitter);

		List<Model> result = sparqlConstructTransformer.transform(inputModel);

		assertThat(result)
				.hasSize(10)
				.are(containsOneModelOf(expectedModelPaths));
	}

	private Condition<Model> containing(Statement statement) {
		return new Condition<>(model -> model.contains(statement), "containing " + statement);
	}

	private Condition<Model> containsOneModelOf(String... expectedModelPaths) {
		Set<Model> expectedModels = Arrays.stream(expectedModelPaths)
				.map(path -> RDFParser.source(path).toModel())
				.collect(Collectors.toSet());

		return new Condition<>(model -> expectedModels.stream().anyMatch(model::isIsomorphicWith), "containing models");
	}

	private Condition<Model> isomorphicWith(String expectedModelPath) {
		Model expectedModel = RDFParser.source(expectedModelPath).toModel();
		return new Condition<>(expectedModel::isIsomorphicWith, "isomorphic with " + expectedModelPath);
	}

	@Test
	void initGeoFunctionsTest() {
		new SparqlConstructTransformer(QueryFactory.create(), false, defaultDatasetSplitter);

		assertThat(FunctionRegistry.get()).has(registeredGeoFunctions());
	}

	private Condition<FunctionRegistry> registeredGeoFunctions() {
		final Stream<String> functionNames = Stream.of(FirstCoordinate.NAME, LastCoordinate.NAME, LineLength.NAME, MidPoint.NAME, PointAtFromStart.NAME, DistanceFromStart.NAME, LineAtIndex.NAME);
		return new Condition<>(registry -> functionNames.allMatch(registry::isRegistered), "contains all geo functions");
	}
}