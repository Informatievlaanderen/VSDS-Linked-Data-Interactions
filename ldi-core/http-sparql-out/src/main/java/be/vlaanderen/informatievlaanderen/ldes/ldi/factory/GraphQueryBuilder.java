package be.vlaanderen.informatievlaanderen.ldes.ldi.factory;

public class GraphQueryBuilder {
	private final String graph;

	public GraphQueryBuilder(String graph) {
		this.graph = graph;
	}

	public InsertFunctionBuilder insert() {
		return InsertFunctionBuilder.withGraph(graph);
	}

	public DeleteFunctionBuilder delete() {
		return DeleteFunctionBuilder.withGraph(graph);
	}
}
