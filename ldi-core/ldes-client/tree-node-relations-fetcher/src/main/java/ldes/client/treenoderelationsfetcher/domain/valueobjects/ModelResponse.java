package ldes.client.treenoderelationsfetcher.domain.valueobjects;

import org.apache.jena.rdf.model.*;

import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class ModelResponse {
	private static final Resource ANY_RESOURCE = null;
	private static final String W3C_TREE = "https://w3id.org/tree#";
	private static final Property W3ID_TREE_RELATION = createProperty(W3C_TREE, "relation");
	public static final Property W3ID_TREE_NODE = createProperty(W3C_TREE, "node");
	private final Model model;

	public ModelResponse(Model model) {
		this.model = model;
	}

	public List<TreeNodeRelation> getTreeNodeRelations() {
		return extractRelations()
				.map(this::mapToTreeNodeRelation)
				.toList();
	}

	private Stream<Statement> extractRelations() {
		return Stream.iterate(model.listStatements(ANY_RESOURCE, W3ID_TREE_RELATION, ANY_RESOURCE),
				Iterator::hasNext, UnaryOperator.identity()).map(Iterator::next);
	}

	private TreeNodeRelation mapToTreeNodeRelation(Statement statement) {
		final Resource relationResource = statement.getResource();
		final StmtIterator relationsStatements = relationResource.listProperties();
		final String relationUri = relationResource.getProperty(W3ID_TREE_NODE).getResource().toString();
		final Model treeNodeRelation = ModelFactory.createDefaultModel()
				.add(statement)
				.add(relationsStatements);

		return new TreeNodeRelation(relationUri, treeNodeRelation);
	}
}
