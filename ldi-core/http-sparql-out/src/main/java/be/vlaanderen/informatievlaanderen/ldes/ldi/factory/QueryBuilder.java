package be.vlaanderen.informatievlaanderen.ldes.ldi.factory;

import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.DeleteFunction;

public class QueryBuilder {
	private QueryBuilder() {
	}

	public static InsertFunctionBuilder insert() {
		return InsertFunctionBuilder.create();
	}

	public static DeleteFunctionBuilder delete() {
		return DeleteFunctionBuilder.create();
	}

	public static DeleteFunction delete(String deleteFunction) {
		return DeleteFunction.ofQuery(deleteFunction);
	}

	public static GraphQueryBuilder withGraph(String graph) {
		return new GraphQueryBuilder(graph);
	}
}
