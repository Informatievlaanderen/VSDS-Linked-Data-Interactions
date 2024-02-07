package ldes.client.treenoderelationsfetcher.domain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TreeNodeRelationTest {

	private TreeNodeRelation treeNodeRelation;

	@BeforeEach
	void setUp() {
		Model relationModel = ModelFactory.createDefaultModel();
		treeNodeRelation = new TreeNodeRelation("https://example.com/relation", relationModel);
	}

	@Test
	void givenNonEmptyModel_whenIsNotEmpty_thenReturnsTrue() {
		Model nonEmptyModel = ModelFactory.createDefaultModel();
		Resource subject = ResourceFactory.createResource("https://example.com/subject");
		Property predicate = ResourceFactory.createProperty("https://example.com/predicate");
		nonEmptyModel.add(subject, predicate, "Object");
		TreeNodeRelation nonEmptyTreeNodeRelation = new TreeNodeRelation("https://example.com/relation", nonEmptyModel);

		assertThat(nonEmptyTreeNodeRelation.isNotEmpty()).isTrue();
	}

	@Test
	void givenEmptyModel_whenIsNotEmpty_thenReturnsFalse() {
		assertThat(treeNodeRelation.isNotEmpty()).isFalse();
	}

	@Test
	void givenModelWithoutTreeNode_whenFromModel_thenThrowsIllegalStateException() {
		Model modelWithoutTreeNode = ModelFactory.createDefaultModel();

		assertThatThrownBy(() -> TreeNodeRelation.fromModel(modelWithoutTreeNode))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("No tree node found for this relation");
	}

	@Test
	void givenModelWithTreeNode_whenFromModel_thenReturnsTreeNodeRelation() {
		String relationUri = "https://example.com/relation";
		Model modelWithTreeNode = ModelFactory.createDefaultModel();
		Resource subject = ResourceFactory.createResource("https://example.com/tree-node");
		Property property = ResourceFactory.createProperty(TreeNodeRelation.W3C_TREE, "node");
		modelWithTreeNode.add(subject, property, relationUri);

		TreeNodeRelation treeNodeRelation = TreeNodeRelation.fromModel(modelWithTreeNode);

		assertThat(treeNodeRelation.getRelationUri()).isEqualTo(relationUri);
		assertThat(treeNodeRelation.getRelationModel()).matches(modelWithTreeNode::isIsomorphicWith);
	}
}
