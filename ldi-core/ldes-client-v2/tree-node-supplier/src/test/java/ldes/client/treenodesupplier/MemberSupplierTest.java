package ldes.client.treenodesupplier;

import ldes.client.requestexecutor.domain.valueobjects.DefaultConfig;
import ldes.client.treenodefetcher.TreeNodeFetcher;
import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryTreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.sqlite.SqliteMemberRepository;
import org.junit.jupiter.api.Test;

class MemberSupplierTest {

	@Test
	void test() {
		MemberSupplier memberSupplier = new MemberSupplier(new Processor(new TreeNodeRecord(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z"),
				new InMemoryTreeNodeRecordRepository(), new SqliteMemberRepository(),
				new TreeNodeFetcher(new DefaultConfig().createRequestExecutor()), false));

		while (true)
			memberSupplier.get();

	}

}