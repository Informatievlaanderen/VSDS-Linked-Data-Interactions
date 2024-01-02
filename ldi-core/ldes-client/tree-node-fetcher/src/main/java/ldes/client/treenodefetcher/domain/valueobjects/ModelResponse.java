package ldes.client.treenodefetcher.domain.valueobjects;

import ldes.client.treenodefetcher.domain.entities.TreeMember;
import org.apache.jena.graph.TripleBoundary;
import org.apache.jena.rdf.model.*;

import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class ModelResponse {

	protected static final Resource ANY_RESOURCE = null;
	protected static final Property ANY_PROPERTY = null;
	public static final String W3C_TREE = "https://w3id.org/tree#";
	public static final Property W3ID_TREE_RELATION = createProperty(W3C_TREE, "relation");
	public static final Property W3ID_TREE_MEMBER = createProperty(W3C_TREE, "member");
	public static final Property W3ID_TREE_NODE = createProperty(W3C_TREE, "node");
	private final ModelExtract modelExtract = new ModelExtract(new StatementTripleBoundary(TripleBoundary.stopNowhere));
	private final Model model;

	public ModelResponse(Model model) {
		this.model = model;
	}

	public List<String> getRelations() {
		return extractRelations(model)
				.map(relationStatement -> relationStatement.getResource()
						.getProperty(W3ID_TREE_NODE).getResource().toString())
				.toList();
	}

	public List<TreeMember> getMembers() {
		return extractMembers()
				.map(memberStatement -> processMember(model, memberStatement))
				.toList();
	}

	private Stream<Statement> extractMembers() {
		StmtIterator memberIterator = model.listStatements(ANY_RESOURCE, W3ID_TREE_MEMBER, ANY_RESOURCE);

		return Stream.iterate(memberIterator, Iterator::hasNext, UnaryOperator.identity())
				.map(Iterator::next);
	}

	private TreeMember processMember(Model treeNodeModel, Statement memberStatement) {
		final Model memberModel = modelExtract.extract(memberStatement.getObject().asResource(), treeNodeModel);
		final String id = memberStatement.getObject().toString();
		return new TreeMember(id, memberModel);
	}

	private Stream<Statement> extractRelations(Model treeNodeModel) {
		return Stream.iterate(treeNodeModel.listStatements(ANY_RESOURCE, W3ID_TREE_RELATION, ANY_RESOURCE),
				Iterator::hasNext, UnaryOperator.identity()).map(Iterator::next);
	}
}
