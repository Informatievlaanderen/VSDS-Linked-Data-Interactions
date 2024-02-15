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
	// TODO: clean up magic strings
	public static final String W3C_TREE = "https://w3id.org/tree#";
	public static final Property W3ID_TREE_RELATION = createProperty(W3C_TREE, "relation");
	private final Logger log = LoggerFactory.getLogger(StartingTreeNodeRelationsFinder.class);
	private final RedirectRequestExecutor requestExecutor;

	public StartingTreeNodeRelationsFinder(RequestExecutor requestExecutor) {
		this.requestExecutor = new RedirectRequestExecutor(requestExecutor);
	}

	public List<StartingTreeNode> findAllStartingTreeNodes(final StartingNodeRequest startingNodeRequest) {
		final Response response = requestExecutor.execute(startingNodeRequest);
		final Model model = getModelFromResponse(startingNodeRequest.lang(), response.getBody().orElseThrow(), startingNodeRequest.url());
		log.atInfo().log("Parsing response for: " + startingNodeRequest.url());
		if (isResponseAnEventStream(startingNodeRequest.url(), model)) {
			return extractStartingNodesFromEventStream(model);
		}
		return extractStartingNodesFromView(model);
	}

	private Model getModelFromResponse(Lang lang, byte[] responseBody, String baseUrl) {
		return RDFParser.source(new ByteArrayInputStream(responseBody)).lang(lang).base(baseUrl).build().toModel();
	}

	private boolean isResponseAnEventStream(String requestedUrl, Model model) {
		return model.contains(model.createResource(requestedUrl), model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), model.createResource("https://w3id.org/ldes#EventStream"));
	}

	private List<StartingTreeNode> extractStartingNodesFromEventStream(Model model) {
		return model
				.listStatements(null, createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), createProperty("https://w3id.org/tree#Node"))
				.toList().stream()
				.map(stmt -> stmt.getSubject().toString())
				.map(StartingTreeNode::new)
				.toList();
	}

	private List<StartingTreeNode> extractStartingNodesFromView(Model model) {
		return model.listStatements(null, W3ID_TREE_RELATION, (Resource) null)
				.toList()
				.stream()
				.map(Statement::getResource)
				.map(resource -> resource.listProperties(model.createProperty("https://w3id.org/tree#node")).nextOptional())
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(statement -> statement.getObject().toString())
				.map(StartingTreeNode::new)
				.toList();
	}

}
