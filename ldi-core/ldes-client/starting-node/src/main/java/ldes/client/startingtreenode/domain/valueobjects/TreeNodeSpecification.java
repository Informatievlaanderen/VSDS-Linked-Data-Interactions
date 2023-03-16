package ldes.client.startingtreenode.domain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

public class TreeNodeSpecification implements StartingNodeSpecification {
	public static final String TREE = "https://w3id.org/tree#";
	public static final Resource TREE_NODE_RESOURCE = createResource(TREE + "Node");
	public static final String RDF_SYNTAX = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final Property RDF_SYNTAX_TYPE = createProperty(RDF_SYNTAX, "type");

	@Override
	public boolean test(Model model) {
		return (getTreeNode(model).isPresent());
	}

	private Optional<Resource> getTreeNode(Model model) {
		return model.listSubjectsWithProperty(RDF_SYNTAX_TYPE, TREE_NODE_RESOURCE)
				.nextOptional();
	}

	@Override
	public StartingTreeNode extractStartingNode(Model model) {
		return getTreeNode(model)
				.map(Resource::getURI)
				.map(StartingTreeNode::new)
				.orElseThrow(() -> new RuntimeException("a"));
	}
}
