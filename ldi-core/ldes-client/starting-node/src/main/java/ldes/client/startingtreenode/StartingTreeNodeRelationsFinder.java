package ldes.client.startingtreenode;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import ldes.client.startingtreenode.domain.valueobjects.StartingNodeRequest;
import ldes.client.startingtreenode.domain.valueobjects.StartingTreeNode;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class StartingTreeNodeRelationsFinder {
	private static final String W3C_TREE = "https://w3id.org/tree#";
	private static final Property W3IC_TREE_RELATION = createProperty(W3C_TREE, "relation");
	private static final Property RDF_SYNTAX_TYPE = createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
	private final Logger log = LoggerFactory.getLogger(StartingTreeNodeRelationsFinder.class);
	private final RedirectRequestExecutor requestExecutor;

	public StartingTreeNodeRelationsFinder(RequestExecutor requestExecutor) {
		this.requestExecutor = new RedirectRequestExecutor(requestExecutor);
	}

	/**
	 * Determines all starting relations that needs to be queued.
	 *
	 * @param startingNodeRequest can contain a collection, view or treeNode.
	 * @return all nodes that must be further queued by the discoverer
	 */
	public List<StartingTreeNode> findAllStartingTreeNodes(final StartingNodeRequest startingNodeRequest) {
		final Response response = requestExecutor.execute(startingNodeRequest);
		final Model model = getModelFromResponse(startingNodeRequest.lang(), response.getBody().orElseThrow(), startingNodeRequest.url());
		log.atInfo().log("Parsing response for: " + startingNodeRequest.url());
		return extractStartingNodes(model);
	}

	private Model getModelFromResponse(Lang lang, byte[] responseBody, String baseUrl) {
		return RDFParser.source(new ByteArrayInputStream(responseBody)).lang(lang).base(baseUrl).build().toModel();
	}

	private List<StartingTreeNode> extractStartingNodes(Model model) {
		if (model.contains(null, W3IC_TREE_RELATION, (Resource) null)) {
			return extractStartingNodesFromTreeNode(model);
		}
		return extractStartingNodesFromEventStream(model);
	}

	private List<StartingTreeNode> extractStartingNodesFromEventStream(Model model) {
		return model
				.listSubjectsWithProperty(RDF_SYNTAX_TYPE, createProperty(W3C_TREE, "Node"))
				.toList()
				.stream()
				.map(Object::toString)
				.map(StartingTreeNode::new)
				.toList();
	}

	private List<StartingTreeNode> extractStartingNodesFromTreeNode(Model model) {
		return model.listStatements(null, createProperty(W3C_TREE, "relation"), (Resource) null)
				.toList()
				.stream()
				.map(Statement::getResource)
				.map(resource -> resource.listProperties(model.createProperty(W3C_TREE, "node")).nextOptional())
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(statement -> statement.getObject().toString())
				.map(StartingTreeNode::new)
				.toList();
	}

}
