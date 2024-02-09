package ldes.client.treenoderelationsfetcher.domain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class TreeNodeRelation {
	public static final String W3C_TREE = "https://w3id.org/tree#";
	public static final Property W3ID_TREE_NODE = createProperty(W3C_TREE, "node");
	private final String relationUri;
	private final Model relationModel;

	public TreeNodeRelation(String relationUri, Model relationModel) {
		this.relationUri = relationUri;
		this.relationModel = relationModel;
	}

	public static TreeNodeRelation fromModel(Model relationModel) {
		final String relationUri = relationModel.listObjectsOfProperty(W3ID_TREE_NODE)
				.nextOptional()
				.map(Object::toString)
				.orElseThrow(() -> new IllegalArgumentException("No tree node found for this relation"));

		return new TreeNodeRelation(relationUri, relationModel);
	}

	public Model getRelationModel() {
		return relationModel;
	}

	public String getRelationUri() {
		return relationUri;
	}

	public boolean isNotEmpty() {
		return !relationModel.isEmpty();
	}
}
