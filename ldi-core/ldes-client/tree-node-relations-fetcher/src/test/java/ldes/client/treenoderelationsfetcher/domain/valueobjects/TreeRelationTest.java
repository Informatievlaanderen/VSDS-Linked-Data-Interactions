package ldes.client.treenoderelationsfetcher.domain.valueobjects;

import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TreeRelationTest {
	@Test
	void givenModelWithoutTreeNode_whenFromModel_thenThrowsIllegalStateException() {
		Model modelWithoutTreeNode = ModelFactory.createDefaultModel();

		assertThatThrownBy(() -> TreeRelation.fromModel(modelWithoutTreeNode))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("No tree node found for this relation");
	}

	@Test
	void givenModelWithTreeNodeAndTreePath_whenFromModel_thenReturnsRequiredTreeRelation() {
		String relationUri = "https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=05";
		Model modelWithTreeNode = RDFDataMgr.loadModel("tree-node-relations/tree-node-relation.ttl");

		TreeRelation treeRelation = TreeRelation.fromModel(modelWithTreeNode);

		assertThat(treeRelation.getUri()).isEqualTo(relationUri);
		assertThat(treeRelation.isRequired()).isTrue();
	}

	@Test
	void givenModelWithTreeNode_whenFromModel_thenReturnsNonRequiredTreeRelation() {
		String relationUri = "https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=05&day=11&hour=06&pageNumber=1";
		Model modelWithTreeNode = RDFDataMgr.loadModel("tree-node-relations/tree-node-relation-with-pagenumber.ttl");

		TreeRelation treeRelation = TreeRelation.fromModel(modelWithTreeNode);

		assertThat(treeRelation.getUri()).isEqualTo(relationUri);
		assertThat(treeRelation.isRequired()).isFalse();
	}
}