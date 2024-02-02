package ldes.client.treenoderelationsfetcher.domain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TreeNodeRelationTest {
	@Test
	void test_GetRelations() {
		final Model relationModel = RDFDataMgr.loadModel("tree-node-relations/tree-node-relation.ttl");
		final TreeNodeRelation treeNodeRelation = new TreeNodeRelation(relationModel);

		String relation = treeNodeRelation.getRelation();

		assertThat(relation)
				.isEqualTo("https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=05");
	}

	@Test
	void test_FailingGetRelations() {
		final Model relationsModel = RDFDataMgr.loadModel("tree-node-relations/malformed-tree-node-relation.ttl");
		final TreeNodeRelation treeNodeRelation = new TreeNodeRelation(relationsModel);

		assertThatThrownBy(treeNodeRelation::getRelation).isInstanceOf(IllegalStateException.class);
	}
}