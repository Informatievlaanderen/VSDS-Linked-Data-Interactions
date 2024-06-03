package ldes.client.treenoderelationsfetcher.domain.valueobjects;

import ldes.client.treenoderelationsfetcher.services.LdesRelationWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

/**
 * Represents all the relations of a single TreeNode of a LDES
 * <br />
 * Is in fact a leaf in the composite pattern
 */
public class TreeRelation implements LdesRelation {
	private static final String W3C_TREE = "https://w3id.org/tree#";
	private static final Property W3ID_TREE_NODE = createProperty(W3C_TREE, "node");
	private static final Property W3ID_TREE_PATH = createProperty(W3C_TREE, "path");
	private final String relationUri;
	private final boolean required;
	private final List<LdesRelation> relations;

	public TreeRelation(String relationUri, boolean required) {
		this.relationUri = relationUri;
		this.required = required;
		this.relations = new ArrayList<>();
	}

	public static TreeRelation fromModel(Model model) {
		final boolean required = extractProperty(model, W3ID_TREE_PATH).isPresent();

		final String relationUri = extractProperty(model, W3ID_TREE_NODE)
				.orElseThrow(() -> new IllegalArgumentException("No tree node found for this relation"));

		return new TreeRelation(relationUri, required);
	}

	public boolean isRequired() {
		return required;
	}

	@Override
	public void addRelation(LdesRelation ldesRelation) {
		relations.add(ldesRelation);
	}

	@Override
	public int countTotalRelations() {
		return countChildRelations() + relations.stream().mapToInt(LdesRelation::countTotalRelations).sum();
	}

	@Override
	public int countChildRelations() {
		return relations.size();
	}

	@Override
	public List<LdesRelation> getRelations() {
		return List.copyOf(relations);
	}

	@Override
	public String getUri() {
		return relationUri;
	}

	/**
	 * @return a tree representation of this tree relation
	 */
	@Override
	public String asString() {
		final LdesRelationWriter ldesRelationWriter = new LdesRelationWriter();
		return ldesRelationWriter.writeToString(this);
	}

	/**
	 * @return a string representation of this tree relation, including a first string statement of how many relations
	 * there are included
	 */
	@Override
	public String toString() {
		return "%s contains a total of %d child relations:%n%s".formatted(relationUri, countTotalRelations(), asString());
	}

	private static Optional<String> extractProperty(Model model, Property node) {
		return model.listObjectsOfProperty(node)
				.nextOptional()
				.map(Object::toString);
	}
}
