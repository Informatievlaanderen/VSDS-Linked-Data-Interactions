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
	private Set<TreeNodeRecord> immutable = new HashSet<>();
	private final Set<TreeNodeRecord> almostImmutable = new HashSet<>();

	public void saveTreeNodeRecord(TreeNodeRecord treeNodeRecord) {
		switch (treeNodeRecord.getTreeNodeStatus()) {
			case NOT_VISITED -> notVisited.add(treeNodeRecord);
			case MUTABLE_AND_ACTIVE -> {
				mutableAndActive.add(treeNodeRecord);
				notVisited.remove(treeNodeRecord);
			}
			case IMMUTABLE_WITHOUT_UNPROCESSED_MEMBERS -> {
				immutable.add(treeNodeRecord);
				almostImmutable.remove(treeNodeRecord);
			}
            case IMMUTABLE_WITH_UNPROCESSED_MEMBERS -> {
				almostImmutable.add(treeNodeRecord);
				notVisited.remove(treeNodeRecord);
            }
        }
	}

	public boolean existsById(String treeNodeId) {
		TreeNodeRecord treeNodeRecord = new TreeNodeRecord(treeNodeId);
		return immutable.contains(treeNodeRecord) ||
				Stream.of(notVisited, mutableAndActive)
				.anyMatch(treeNodeRecords -> treeNodeRecords.contains(treeNodeRecord));
	}

	@Override
	public boolean containsTreeNodeRecords() {
		return Stream.of(notVisited, mutableAndActive, immutable)
				.anyMatch(treeNodeRecords -> !treeNodeRecords.isEmpty());
	}

	@Override
	public void resetContext() {
		// no context to reset
	}

	public boolean existsByIdAndStatus(String treeNodeId, TreeNodeStatus treeNodeStatus) {
		return switch (treeNodeStatus) {
			case NOT_VISITED -> notVisited.contains(new TreeNodeRecord(treeNodeId));
			case MUTABLE_AND_ACTIVE -> mutableAndActive.contains(new TreeNodeRecord(treeNodeId));
			case IMMUTABLE_WITH_UNPROCESSED_MEMBERS -> almostImmutable.contains(new TreeNodeRecord(treeNodeId));
			case IMMUTABLE_WITHOUT_UNPROCESSED_MEMBERS -> immutable.contains(new TreeNodeRecord(treeNodeId));
		};
	}

	@Override
	public void destroyState() {
		notVisited = new ArrayList<>();
		mutableAndActive = new PriorityQueue<>(new TreeNodeRecordComparator());
		immutable = new HashSet<>();
	}

	public Optional<TreeNodeRecord> getTreeNodeRecordWithStatusAndEarliestNextVisit(TreeNodeStatus treeNodeStatus) {
		return switch (treeNodeStatus) {
			case NOT_VISITED -> notVisited.isEmpty() ? Optional.empty() : Optional.of(notVisited.get(0));
			case MUTABLE_AND_ACTIVE ->
				mutableAndActive.isEmpty() ? Optional.empty() : Optional.of(mutableAndActive.poll());
			case IMMUTABLE_WITH_UNPROCESSED_MEMBERS
					-> almostImmutable.isEmpty() ? Optional.empty() : almostImmutable.stream().findFirst();
			case IMMUTABLE_WITHOUT_UNPROCESSED_MEMBERS
					-> immutable.isEmpty() ? Optional.empty() : immutable.stream().findFirst();
		};
	}
}
