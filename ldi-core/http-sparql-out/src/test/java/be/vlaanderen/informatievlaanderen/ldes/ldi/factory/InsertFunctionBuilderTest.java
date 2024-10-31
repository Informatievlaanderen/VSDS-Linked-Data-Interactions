package be.vlaanderen.informatievlaanderen.ldes.ldi.factory;

import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.InsertFunction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InsertFunctionBuilderTest {
	@Test
	void test_Build() {
		final InsertFunction expected = new InsertFunction("INSERT DATA { %s }");

		final InsertFunction result = QueryBuilder.insert().build();

		assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(expected);
	}

	@Test
	void given_Graph_test_Build() {
		final InsertFunction expected = new InsertFunction("INSERT DATA { GRAPH <http://example.com> { %s } }");

		final InsertFunction result = QueryBuilder.withGraph("http://example.com").insert().build();

		assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(expected);
	}
}