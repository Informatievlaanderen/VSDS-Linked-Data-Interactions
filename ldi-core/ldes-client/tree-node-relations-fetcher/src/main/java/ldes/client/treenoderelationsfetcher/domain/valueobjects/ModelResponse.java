package ldes.client.treenoderelationsfetcher.domain.valueobjects;

import org.apache.jena.rdf.model.*;

import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

/**
 * Wrapper around the RDF TreeNode response to more easily extract the relations from that response
 */
public class ModelResponse {
	private static final Resource ANY_RESOURCE = null;
	private static final String W3C_TREE = "https://w3id.org/tree#";
	private static final Property W3ID_TREE_RELATION = createProperty(W3C_TREE, "relation");
	private final Model model;

	public ModelResponse(Model model) {
		this.model = model;
	}

	public List<TreeRelation> getTreeRelations() {
		return extractRelations()
				.map(this::mapToTreeRelation)
				.toList();
	}

	private Stream<Statement> extractRelations() {
		return Stream.iterate(model.listStatements(ANY_RESOURCE, W3ID_TREE_RELATION, ANY_RESOURCE),
				Iterator::hasNext, UnaryOperator.identity()).map(Iterator::next);
	}

	private TreeRelation mapToTreeRelation(Statement statement) {
		final Resource relationResource = statement.getResource();
		final StmtIterator relationsStatements = relationResource.listProperties();
		final Model treeNodeRelation = ModelFactory.createDefaultModel()
				.add(statement)
				.add(relationsStatements);

		return TreeRelation.fromModel(treeNodeRelation);
	}
}
