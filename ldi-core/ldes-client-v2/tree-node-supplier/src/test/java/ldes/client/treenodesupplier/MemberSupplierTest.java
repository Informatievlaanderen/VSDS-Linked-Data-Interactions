package ldes.client.treenodesupplier;

import ldes.client.requestexecutor.domain.valueobjects.DefaultConfig;
import ldes.client.treenodefetcher.TreeNodeFetcher;
import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import org.junit.jupiter.api.Test;

class MemberSupplierTest {

	@Test
	void test() {
		MemberSupplier memberSupplier = new MemberSupplier(new Processor(new TreeNodeRecord(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z"),
				new InMemoryTreeNodeRecordRepository(), new InMemoryMemberRepository(),
				new TreeNodeFetcher(new DefaultConfig().createRequestExecutor())));

		memberSupplier.get();

	}

}