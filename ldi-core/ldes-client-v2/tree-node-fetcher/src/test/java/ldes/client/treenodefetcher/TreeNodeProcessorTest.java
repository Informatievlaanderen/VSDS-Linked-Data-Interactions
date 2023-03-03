package ldes.client.treenodefetcher;

import ldes.client.treenodefetcher.domain.entities.TreeNode;
import org.junit.jupiter.api.Test;

class TreeNodeProcessorTest {

	@Test
	void test() {
		TreeNodeProcessor treeNodeProcessor = new TreeNodeProcessor();
		TreeNode process = treeNodeProcessor.process(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2021-10-20T12:05:32.563Z");
		System.out.println();
	}

}