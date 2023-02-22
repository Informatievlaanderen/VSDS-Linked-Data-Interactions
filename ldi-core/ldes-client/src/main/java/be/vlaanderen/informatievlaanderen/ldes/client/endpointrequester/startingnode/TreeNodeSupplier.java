package be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.startingnode;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

public class TreeNodeSupplier extends AbstractStartingNodeSupplier {

	public static final Resource TREE_NODE_RESOURCE = createResource(TREE + "Node");
	public static final String RDF_SYNTAX = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final Property RDF_SYNTAX_TYPE = createProperty(RDF_SYNTAX, "type");

	public TreeNodeSupplier(AbstractStartingNodeSupplier nextHandler) {
		super(nextHandler);
	}

	@Override
	public Optional<StartingNode> getStartingNode(Model model) {
		Optional<Resource> treeNode = model.listSubjectsWithProperty(RDF_SYNTAX_TYPE, TREE_NODE_RESOURCE)
				.nextOptional();
		if (treeNode.isPresent()) {
			return treeNode
					.map(Resource::getURI)
					.map(StartingNode::new);
		} else {
			return super.getStartingNode(model);
		}
	}

}
