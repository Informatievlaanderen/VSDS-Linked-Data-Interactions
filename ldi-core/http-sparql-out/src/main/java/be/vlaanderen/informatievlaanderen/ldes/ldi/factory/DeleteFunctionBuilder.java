package be.vlaanderen.informatievlaanderen.ldes.ldi.factory;

import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.DeleteFunction;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DeleteFunctionBuilder {
	private static final String BASE_TEMPLATE = "DELETE %s { ?s ?p ?o } WHERE { %%s }";
	private final String base;

	private DeleteFunctionBuilder(String base) {
		this.base = base;
	}

	public static DeleteFunction disabled() {
		return DeleteFunction.empty();
	}

	public static DeleteFunctionBuilder create() {
		return new DeleteFunctionBuilder(String.format(BASE_TEMPLATE, ""));
	}

	public static DeleteFunctionBuilder withGraph(String graph) {
		final String graphBase = "FROM <%s>".formatted(graph);
		return new DeleteFunctionBuilder(String.format(BASE_TEMPLATE, graphBase));
	}

	public DeleteFunction withDepth(int depth) {
		if (depth < 1) {
			throw new IllegalArgumentException("Depth must be greater than 0");
		}
		final String whereConditions = IntStream.range(1, depth + 1)
				.mapToObj(DeleteFunctionBuilder::createUnionClause)
				.collect(Collectors.joining("",  """
						    {
						        VALUES ?o0 { <%s> }
						        ?o0 ?p ?o .
						        BIND (?o0 AS ?s)
						    }
						""", ""));
		final String query = base.formatted(whereConditions);
		return DeleteFunction.ofQuery(query);
	}

	private static String createUnionClause(int length) {
		return IntStream.range(0, length)
				.mapToObj(i -> "?o%d ?p%d ?o%d . ".formatted(i, i + 1, i + 1))
				.collect(Collectors.joining("",
						" UNION { ",
						"?o%d ?p ?o . BIND (?o%d AS ?s)}".formatted(length, length)));
	}
}
