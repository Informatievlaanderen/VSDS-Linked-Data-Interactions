package be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.startingnode;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class ViewNodeSupplier extends AbstractStartingNodeSupplier {

	public static final Property TREE_VIEW = createProperty(TREE, "view");

	public ViewNodeSupplier(AbstractStartingNodeSupplier nextHandler) {
		super(nextHandler);
	}

	@Override
	public Optional<StartingNode> getStartingNode(Model model) {
		Optional<RDFNode> viewNode = model.listObjectsOfProperty(TREE_VIEW).nextOptional();
		if (viewNode.isPresent()) {
			return viewNode
					.map(RDFNode::asResource)
					.map(Resource::getURI)
					.map(StartingNode::new);
		} else {
			return super.getStartingNode(model);
		}
	}

}
