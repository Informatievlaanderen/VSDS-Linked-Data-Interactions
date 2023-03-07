package ldes.client.treenodefetcher;

import ldes.client.treenodefetcher.domain.entities.TreeNode;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.Test;

import java.util.List;

class TreeNodeFetcherTest {

	// TODO write proper test
	@Test
	void test() {
		TreeNodeFetcher treeNodeFetcher = new TreeNodeFetcher();
		TreeNode treeNode = treeNodeFetcher.fetchTreeNode(
				new TreeNodeRequest(
						"https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2021-10-20T12:05:32.563Z",
						Lang.JSONLD));
		List<String> relations = treeNode.getRelations();
		String treeNodeId = treeNode.getTreeNodeId();
		System.out.println();
	}

}