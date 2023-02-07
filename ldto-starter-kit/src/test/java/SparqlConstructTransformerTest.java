import be.vlaanderen.informatievlaanderen.ldes.ldto.transformer.SparqlConstructTransformer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

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
		Map<String, String> config = new HashMap<>();
		config.put("query", constructQuery);
		SparqlConstructTransformer sparqlConstructTransformer = new SparqlConstructTransformer();
		sparqlConstructTransformer.init(config);

		Model model = RDFParserBuilder.create().fromString(content).lang(Lang.NQUADS).toModel();

		model = sparqlConstructTransformer.execute(model);

		assertTrue(model.containsLiteral(model.createResource("http://data-in-flowfile/"),
				model.createProperty("http://xmlns.com/foaf/0.1/name"),
				model.createLiteral("What's my name")));

	}
}
