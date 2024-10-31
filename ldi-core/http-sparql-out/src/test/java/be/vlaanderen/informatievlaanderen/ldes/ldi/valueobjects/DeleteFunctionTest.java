package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteFunctionTest {
	@Test
	void test_GetQueryForResource() {
		final DeleteFunction query = DeleteFunction.ofQuery("DELETE { ?s ?p ?o } WHERE { ?s ?p ?o  VALUES ?s { <%s> } }");

		final Optional<String> queryForResource = query.createQueryForResources("http://example.com");

		assertThat(queryForResource)
				.get(InstanceOfAssertFactories.STRING)
				.isEqualToIgnoringWhitespace("DELETE { ?s ?p ?o } WHERE { ?s ?p ?o  VALUES ?s { <http://example.com> } }");
	}

	@Test
	void test_GetQueryForResources() {
		final DeleteFunction query = DeleteFunction.ofQuery("DELETE { ?s ?p ?o } WHERE { ?s ?p ?o  VALUES ?s { <%s> } }");

		final Optional<String> queryForResource = query.createQueryForResources("http://example.com", "http://example.org");

		assertThat(queryForResource)
				.get(InstanceOfAssertFactories.STRING)
				.isEqualToIgnoringWhitespace("DELETE { ?s ?p ?o } WHERE { ?s ?p ?o  VALUES ?s { <http://example.com> <http://example.org> } }");

	}

	@Test
	void given_queryWithoutTemplate_test_GetQueryForResource() {
		final DeleteFunction query = DeleteFunction.ofQuery("DELETE { ?s ?p ?o } WHERE { ?s ?p ?o  VALUES ?s { <http://example.com> } }");

		final Optional<String> queryForResource = query.createQueryForResources("https://fantasy.com");

		assertThat(queryForResource)
				.get(InstanceOfAssertFactories.STRING)
				.isEqualToIgnoringWhitespace("DELETE { ?s ?p ?o } WHERE { ?s ?p ?o  VALUES ?s { <http://example.com> } }");
	}

	@Test
	void given_EmptyDeleteFunction() {
		final DeleteFunction query = DeleteFunction.empty();

		final Optional<String> queryForResource = query.createQueryForResources("https://fantasy.com");

		assertThat(queryForResource).isEmpty();
	}
}