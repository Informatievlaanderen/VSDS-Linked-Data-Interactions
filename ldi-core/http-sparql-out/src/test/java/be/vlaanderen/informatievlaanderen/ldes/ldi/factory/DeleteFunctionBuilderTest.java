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
				DELETE DATA { ?s ?p ?o }
				WHERE {
				    {
				    	VALUES ?o0 { <http://example.com> }
				        ?o0 ?p ?o .
				        BIND (?o0 AS ?s)
				    }
				    UNION {
				        ?o0 ?p1 ?o1 .
				        ?o1 ?p ?o .
				        BIND (?o1 AS ?s)
				    }
				}""";

		final DeleteFunction query = QueryBuilder.delete().withDepth(1);

		assertThat(query.createQueryForResources("http://example.com"))
				.get(InstanceOfAssertFactories.STRING)
				.isEqualToIgnoringWhitespace(expected);
	}

	@Test
	void test_FromDepth3() {
		final String expected = """
				DELETE DATA { ?s ?p ?o }
				WHERE {
				    {
				        VALUES ?o0 { <http://example.com> }
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
				}""";

		final DeleteFunction query = QueryBuilder.delete().withDepth(3);

		assertThat(query.createQueryForResources("http://example.com"))
				.get(InstanceOfAssertFactories.STRING)
				.isEqualToIgnoringWhitespace(expected);
	}

	@Test
	void test_Disabled() {
		final DeleteFunction query = QueryBuilder.delete().disabled();

		assertThat(query)
				.usingRecursiveComparison()
				.isEqualTo(DeleteFunction.empty());
	}

	@Test
	void test_WithQuery() {
		final DeleteFunction expected = DeleteFunction.ofQuery("DELETE { ?s ?p ?o } WHERE { ?s ?p ?o  VALUES ?s { <http://example.com> } }");

		final DeleteFunction result = QueryBuilder.delete("DELETE { ?s ?p ?o } WHERE { ?s ?p ?o  VALUES ?s { <http://example.com> } }");

		assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(expected);
	}


	@Test
	void given_Graph_test_FromDepth1() {
		final String expected = """
				DELETE DATA {
					GRAPH <http://example.org/graph> {
						?s ?p ?o
					}
				}
				WHERE {
					GRAPH <http://example.org/graph> {
						{
							VALUES ?o0 { <http://example.com> }
							?o0 ?p ?o .
							BIND (?o0 AS ?s)
						}
						UNION {
						    ?o0 ?p1 ?o1 .
						    ?o1 ?p ?o .
						    BIND (?o1 AS ?s)
						}
					}
				}""";

		final DeleteFunction query = QueryBuilder.withGraph("http://example.org/graph").delete().withDepth(1);

		assertThat(query.createQueryForResources("http://example.com"))
				.get(InstanceOfAssertFactories.STRING)
				.isEqualToIgnoringWhitespace(expected);
	}

	@Test
	void given_DepthIsZero_when_Build_then_ThrowException() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> QueryBuilder.delete().withDepth(0))
				.withMessage("Depth must be greater than 0");
	}
}