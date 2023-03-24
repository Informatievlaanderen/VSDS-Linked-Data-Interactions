package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

		model = sparqlConstructTransformer.apply(model);

		assertTrue(model.contains(transformedData));
		assertFalse(model.contains(originalData));

	}

	@Test
	void when_executeTransform_includeOriginal_ExpectTransformedModelWithOriginal() {
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer(
				QueryFactory.create(constructQuery), true);

		Model model = ModelFactory.createDefaultModel().add(originalData);

		model = sparqlConstructTransformer.apply(model);

		assertTrue(model.contains(transformedData));
		assertTrue(model.contains(originalData));

	}
}
