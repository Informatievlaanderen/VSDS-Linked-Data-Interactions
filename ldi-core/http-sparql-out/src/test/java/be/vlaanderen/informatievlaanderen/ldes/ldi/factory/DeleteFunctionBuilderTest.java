package be.vlaanderen.informatievlaanderen.ldes.ldi.factory;

import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.DeleteFunction;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class DeleteFunctionBuilderTest {

	@Test
	void test_FromDepth1() {
		final String expected = """
				DELETE { ?s ?p ?o }
				WHERE {
				    {
				        ?o0 ?p ?o .
				        BIND (?o0 AS ?s)
				    }
				    UNION {
				        ?o0 ?p1 ?o1 .
				        ?o1 ?p ?o .
				        BIND (?o1 AS ?s)
				    }
				    FILTER (?o0 IN (<http://example.com>, <http://example.org>))
				}""";

		final DeleteFunction query = DeleteFunctionBuilder.create().withDepth(1);

		assertThat(query.createQueryForResources("http://example.com", "http://example.org"))
				.get(InstanceOfAssertFactories.STRING)
				.isEqualToIgnoringWhitespace(expected);
	}

	@Test
	void test_FromDepth3() {
		final String expected = """
				DELETE { ?s ?p ?o }
				WHERE {
				    {
				        ?o0 ?p ?o .
				        BIND (?o0 AS ?s)
				    }
				    UNION {
				        ?o0 ?p1 ?o1 .
				        ?o1 ?p ?o .
				        BIND (?o1 AS ?s)
				    }
				    UNION {
				        ?o0 ?p1 ?o1 .
				        ?o1 ?p2 ?o2 .
				        ?o2 ?p ?o .
				        BIND (?o2 AS ?s)
				    }
				    UNION {
				        ?o0 ?p1 ?o1 .
				        ?o1 ?p2 ?o2 .
				        ?o2 ?p3 ?o3 .
				        ?o3 ?p ?o .
				        BIND (?o3 AS ?s)
				    }
				    FILTER (?o0 IN (<http://example.com>))
				}""";

		final DeleteFunction query = DeleteFunctionBuilder.create().withDepth(3);

		assertThat(query.createQueryForResources("http://example.com"))
				.get(InstanceOfAssertFactories.STRING)
				.isEqualToIgnoringWhitespace(expected);
	}

	@Test
	void test_Disabled() {
		final DeleteFunction query = DeleteFunctionBuilder.disabled();

		assertThat(query)
				.usingRecursiveComparison()
				.isEqualTo(DeleteFunction.empty());
	}

	@Test
	void given_Graph_test_FromDepth1() {
		final String expected = """
				DELETE FROM <http://example.org/graph> {
					?s ?p ?o
				}
				WHERE {
					{
						?o0 ?p ?o .
						BIND (?o0 AS ?s)
					}
					UNION {
					    ?o0 ?p1 ?o1 .
					    ?o1 ?p ?o .
					    BIND (?o1 AS ?s)
					}
				    FILTER (?o0 IN (<http://example.com>))
				}""";

		final DeleteFunction query = DeleteFunctionBuilder.withGraph("http://example.org/graph").withDepth(1);

		assertThat(query.createQueryForResources("http://example.com"))
				.get(InstanceOfAssertFactories.STRING)
				.isEqualToIgnoringWhitespace(expected);
	}

	@Test
	void given_DepthIsNegative_when_Build_then_ThrowException() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> DeleteFunctionBuilder.create().withDepth(-1))
				.withMessage("Depth must be a positive number");
	}
}