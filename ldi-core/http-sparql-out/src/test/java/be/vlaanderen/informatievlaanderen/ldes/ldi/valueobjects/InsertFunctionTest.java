package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.update.UpdateAction;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InsertFunctionTest {
	private static final String QUERY = "INSERT DATA { %s }";
	private InsertFunction insertFunction;

	@BeforeEach
	void setUp() {
		insertFunction = new InsertFunction(QUERY);
	}

	@Test
	void test_createQuery() {
		final Model input = RDFParser.source("jane-doe.nt").lang(Lang.NT).toModel();

		final String result = insertFunction.createQuery(input);

		assertThat(result).is(queryIsomorphicWith(input));
	}

	private Condition<String> queryIsomorphicWith(Model model) {
		return new Condition<>(query -> {
			Model output = ModelFactory.createDefaultModel();
			UpdateAction.parseExecute(query, output);
			return model.isIsomorphicWith(output);
		}, "INSERT query must be isomorphic with expected model");
	}
}