package ldes.client.treenodesupplier.repository.inmemory;

import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.services.TreeNodeRecordComparator;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;

import java.util.*;
import java.util.stream.Stream;

public class InMemoryTreeNodeRecordRepository implements TreeNodeRecordRepository {

	private List<TreeNodeRecord> notVisited = new ArrayList<>();
	private PriorityQueue<TreeNodeRecord> mutableAndActive = new PriorityQueue<>(new TreeNodeRecordComparator());
	private List<TreeNodeRecord> immutable = new ArrayList<>();

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

	@Override
	public void destroyState() {
		notVisited = new ArrayList<>();
		mutableAndActive = new PriorityQueue<>(new TreeNodeRecordComparator());
		immutable = new ArrayList<>();
	}

	public Optional<TreeNodeRecord> getOneTreeNodeRecordWithStatus(TreeNodeStatus treeNodeStatus) {
		return switch (treeNodeStatus) {
			case NOT_VISITED -> notVisited.isEmpty() ? Optional.empty() : Optional.of(notVisited.get(0));
			case MUTABLE_AND_ACTIVE ->
				mutableAndActive.isEmpty() ? Optional.empty() : Optional.of(mutableAndActive.poll());
			case IMMUTABLE -> immutable.isEmpty() ? Optional.empty() : Optional.of(immutable.get(0));
		};
	}
}
