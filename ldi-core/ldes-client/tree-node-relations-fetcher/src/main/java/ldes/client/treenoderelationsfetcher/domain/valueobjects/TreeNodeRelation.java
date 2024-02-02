package ldes.client.treenoderelationsfetcher.domain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class TreeNodeRelation {
	public static final String W3C_TREE = "https://w3id.org/tree#";
	public static final Property W3ID_TREE_NODE = createProperty(W3C_TREE, "node");
	private final Model relationModel;

	public TreeNodeRelation(Model relationModel) {
		this.relationModel = relationModel;
	}

	public Model getRelationModel() {
		return relationModel;
	}

	public String getRelation() {
		return relationModel.listObjectsOfProperty(W3ID_TREE_NODE)
				.nextOptional()
				.map(Object::toString)
				.orElseThrow(() -> new IllegalStateException("No tree node found for this relation"));
	}

}
