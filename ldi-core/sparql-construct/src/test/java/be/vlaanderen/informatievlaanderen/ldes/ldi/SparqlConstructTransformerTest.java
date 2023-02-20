package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SparqlConstructTransformerTest {

	private final static String content = """
			<http://data-in-flowfile/> <http://test/> "What's my name" .
			""";

	private final static String constructQuery = """
			PREFIX foaf:   <http://xmlns.com/foaf/0.1/>
			CONSTRUCT {?s foaf:name ?o} WHERE {?s ?p ?o}
			""";

	@Test
	void when_executeTransform_ExpectTransformedModel() {
		SparqlConstructTransformer sparqlConstructTransformer =
				new SparqlConstructTransformer(QueryFactory.create(constructQuery), false);

		Model model = RDFParserBuilder.create().fromString(content).lang(Lang.NQUADS).toModel();

		model = sparqlConstructTransformer.transform(model);

		assertTrue(model.containsLiteral(model.createResource("http://data-in-flowfile/"),
				model.createProperty("http://xmlns.com/foaf/0.1/name"),
				model.createLiteral("What's my name")));

	}
}
