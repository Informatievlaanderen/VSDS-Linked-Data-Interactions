package ldes.client.treenodesupplier;

import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.services.TreeNodeRecordComparator;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;

import java.util.*;
import java.util.stream.Stream;

public class InMemoryTreeNodeRecordRepository implements TreeNodeRecordRepository {

	List<TreeNodeRecord> notVisited = new ArrayList<>();
	PriorityQueue<TreeNodeRecord> mutableAndActive = new PriorityQueue<>(new TreeNodeRecordComparator());
	List<TreeNodeRecord> immutable = new ArrayList<>();

	public void saveTreeNodeRecord(TreeNodeRecord treeNodeRecord) {
		switch (treeNodeRecord.getTreeNodeStatus()) {
			case NOT_VISITED -> notVisited.add(treeNodeRecord);
			case MUTABLE_AND_ACTIVE -> {
				mutableAndActive.add(treeNodeRecord);
				notVisited.remove(treeNodeRecord);
			}
			case IMMUTABLE -> {
				immutable.add(treeNodeRecord);
				notVisited.remove(treeNodeRecord);
				mutableAndActive.remove(treeNodeRecord);
			}
		}
	}

	public boolean existsById(String treeNodeId) {
		return Stream.of(notVisited, mutableAndActive, immutable)
				.anyMatch(treeNodeRecords -> treeNodeRecords.contains(new TreeNodeRecord(treeNodeId)));
	}

	public boolean existsByIdAndStatus(String treeNodeId, TreeNodeStatus treeNodeStatus) {
		return switch (treeNodeStatus) {
			case NOT_VISITED -> notVisited.contains(new TreeNodeRecord(treeNodeId));
			case MUTABLE_AND_ACTIVE -> mutableAndActive.contains(new TreeNodeRecord(treeNodeId));
			case IMMUTABLE -> immutable.contains(new TreeNodeRecord(treeNodeId));
		};
	}

	public Optional<TreeNodeRecord> getOneTreeNodeRecordWithStatus(TreeNodeStatus treeNodeStatus) {
		return switch (treeNodeStatus) {
			case NOT_VISITED -> notVisited.isEmpty() ? Optional.empty() : Optional.of(notVisited.get(0));
			case MUTABLE_AND_ACTIVE ->
				mutableAndActive.isEmpty() ? Optional.empty() : Optional.of(mutableAndActive.peek());
			case IMMUTABLE -> immutable.isEmpty() ? Optional.empty() : Optional.of(immutable.get(0));
		};
	}
}
