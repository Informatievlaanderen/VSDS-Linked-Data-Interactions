package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.*;
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
	void shouldSplitModels_whenQueryContainsGraph_AndHasDefaultModelStatements() throws IOException {
		// TODO TVB: 06/09/23 traffic impl
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
