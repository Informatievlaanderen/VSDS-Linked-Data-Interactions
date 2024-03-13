package ldes.client.treenodesupplier.domain.services;

import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TreeNodeRecordComparatorTest {

	private final TreeNodeRecordComparator treeNodeRecordComparator = new TreeNodeRecordComparator();

	@Test
	void when_TwoTreeNodeRecordsAreCompared_TheOneWithTheEarliestNextVisitIsReturned() {
		TreeNodeRecord treeNodeRecord = getTreeNodeRecord(1, "id1");
		TreeNodeRecord secondTreeNodeRecord = getTreeNodeRecord(2, "id2");
		Optional<TreeNodeRecord> min = Stream.of(treeNodeRecord, secondTreeNodeRecord).min(treeNodeRecordComparator);

		assertEquals(treeNodeRecord, min.get());
	}

	private TreeNodeRecord getTreeNodeRecord(int second, String treeNodeUrl) {
		LocalDateTime firstLocalDateTime = LocalDateTime.of(1, 1, 1, 1, 1, second);
		return new TreeNodeRecord(treeNodeUrl, TreeNodeStatus.MUTABLE_AND_ACTIVE, firstLocalDateTime, new ArrayList<>());
	}

}