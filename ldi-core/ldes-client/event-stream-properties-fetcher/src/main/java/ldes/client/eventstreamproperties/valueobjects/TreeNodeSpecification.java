package ldes.client.eventstreamproperties.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class TreeNodeSpecification implements StartingNodeSpecification {
	public static final String DC_TERMS = "http://purl.org/dc/terms/";
	public static final Property IS_PART_OF = createProperty(DC_TERMS, "isPartOf");
	private final Model model;

	public TreeNodeSpecification(Model model) {
		this.model = model;
	}

	@Override
	public EventStreamProperties extractEventStreamProperties() {
		return extractTreeNode(model).filter(RDFNode::isURIResource)
				.map(RDFNode::asResource)
				.map(node -> new EventStreamProperties(node.getURI()))
				.orElseThrow();
	}

	public static boolean isTreeNode(Model model) {
		return extractTreeNode(model).isPresent();
	}

	private static Optional<RDFNode> extractTreeNode(Model model) {
		return model.listObjectsOfProperty(IS_PART_OF).nextOptional();
	}

}
