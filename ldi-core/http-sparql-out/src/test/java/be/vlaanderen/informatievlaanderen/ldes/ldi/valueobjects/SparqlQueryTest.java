package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.factory.DeleteFunctionBuilder;
import be.vlaanderen.informatievlaanderen.ldes.ldi.factory.InsertFunctionBuilder;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SparqlQueryTest {
	private SparqlQuery sparqlQuery;

	@BeforeEach
	void setUp() {
		sparqlQuery = new SparqlQuery(
				InsertFunctionBuilder.create().build(),
				DeleteFunctionBuilder.create().withDepth(0)
		);
	}

	@Test
	void test() {
		final Model input = RDFParser.create().fromString(singleTriple()).lang(Lang.NT).toModel();

		final String result = sparqlQuery.getQueryForModel(input);

		assertThat(result).isEqualToIgnoringWhitespace(expectedQuery());
	}

	private static String expectedQuery() {
		return  """
				DELETE { ?s ?p ?o }
				WHERE {
				    {
				        VALUES ?o0 { <http://localhost:8080/people> }
				        ?o0 ?p ?o .
				        BIND (?o0 AS ?s)
				    }
				}
				INSERT DATA { <http://localhost:8080/people> <http://schema.org/name> "Jane Doe" . }""";
	}

	private String singleTriple() {
		return "<http://localhost:8080/people> <http://schema.org/name> \"Jane Doe\" .";
	}
}