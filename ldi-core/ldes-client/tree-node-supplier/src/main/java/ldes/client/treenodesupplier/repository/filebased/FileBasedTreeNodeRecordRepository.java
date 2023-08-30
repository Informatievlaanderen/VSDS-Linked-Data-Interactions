package ldes.client.treenodesupplier.repository.filebased;

import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.filebased.mapper.TreeNodeRecordMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBasedTreeNodeRecordRepository implements TreeNodeRecordRepository {
	public static final String NOT_VISITED_TREE_NODES = "treeNodesNotVisited.txt";
	public static final String MUTABLE_TREE_NODES = "treeNodesMutable.txt";
	public static final String IMMUTABLE_TREE_NODES = "treeNodesImmutable.txt";
	private final FileManagerFactory fileManagerFactory;
	private final FileManager fileManager;
	private final TreeNodeRecordMapper mapper = new TreeNodeRecordMapper();
	private final String instanceName;

	public FileBasedTreeNodeRecordRepository(String instanceName) {
		this.fileManagerFactory = FileManagerFactory.getInstance(instanceName);
		this.fileManager = fileManagerFactory.getFileManager(instanceName);
		this.instanceName = instanceName;
	}

	@Override
	public void saveTreeNodeRecord(TreeNodeRecord treeNodeRecord) {
		switch (treeNodeRecord.getTreeNodeStatus()) {
			case NOT_VISITED ->
				fileManager.appendRecord(NOT_VISITED_TREE_NODES, mapper.fromTreeNodeRecord(treeNodeRecord));
			case MUTABLE_AND_ACTIVE -> {
				fileManager.appendRecord(MUTABLE_TREE_NODES, mapper.fromTreeNodeRecord(treeNodeRecord));
				removeTreeNodeRecord(NOT_VISITED_TREE_NODES, treeNodeRecord);
			}
			case IMMUTABLE -> {
				fileManager.appendRecord(IMMUTABLE_TREE_NODES, mapper.fromTreeNodeRecord(treeNodeRecord));
				removeTreeNodeRecord(NOT_VISITED_TREE_NODES, treeNodeRecord);
				removeTreeNodeRecord(MUTABLE_TREE_NODES, treeNodeRecord);
			}
		}
	}

	private void removeTreeNodeRecord(String treeNodesFile, TreeNodeRecord treeNodeRecord) {
		List<TreeNodeRecord> treeNodeRecords = fileManager
				.getRecords(treeNodesFile)
				.map(mapper::toTreeNodeRecord)
				.collect(Collectors.toList());
		treeNodeRecords.remove(treeNodeRecord);
		fileManager.createNewRecords(treeNodesFile, treeNodeRecords.stream().map(mapper::fromTreeNodeRecord));
	}

	@Override
	public boolean existsById(String treeNodeId) {
		return Stream.of(NOT_VISITED_TREE_NODES, MUTABLE_TREE_NODES, IMMUTABLE_TREE_NODES)
				.flatMap(fileManager::getRecords)
				.map(mapper::toTreeNodeRecord)
				.anyMatch(treeNodeRecord -> treeNodeRecord.getTreeNodeUrl().equals(treeNodeId));
	}

	@Override
	public Optional<TreeNodeRecord> getOneTreeNodeRecordWithStatus(TreeNodeStatus treeNodeStatus) {
		return switch (treeNodeStatus) {
			case NOT_VISITED -> getTreeNodeRecordFromFile(NOT_VISITED_TREE_NODES);
			case MUTABLE_AND_ACTIVE -> getTreeNodeRecordFromFile(MUTABLE_TREE_NODES);
			case IMMUTABLE -> getTreeNodeRecordFromFile(IMMUTABLE_TREE_NODES);
		};
	}

	private Optional<TreeNodeRecord> getTreeNodeRecordFromFile(String file) {
		Optional<TreeNodeRecord> treeNodeRecord = fileManager.getRecords(file).map(mapper::toTreeNodeRecord).findAny();
		treeNodeRecord.ifPresent(nodeRecord -> removeTreeNodeRecord(file, nodeRecord));
		return treeNodeRecord;
	}

	@Override
	public boolean existsByIdAndStatus(String treeNodeId, TreeNodeStatus treeNodeStatus) {
		return switch (treeNodeStatus) {
			case NOT_VISITED -> fileContainsTreeNodeRecord(NOT_VISITED_TREE_NODES, treeNodeId);
			case MUTABLE_AND_ACTIVE -> fileContainsTreeNodeRecord(MUTABLE_TREE_NODES, treeNodeId);
			case IMMUTABLE -> fileContainsTreeNodeRecord(IMMUTABLE_TREE_NODES, treeNodeId);
		};
	}

	private boolean fileContainsTreeNodeRecord(String file, String treeNodeId) {
		return fileManager
				.getRecords(file)
				.map(mapper::toTreeNodeRecord)
				.anyMatch(treeNodeRecord -> treeNodeRecord.getTreeNodeUrl().equals(treeNodeId));
	}

	@Override
	public void destroyState() {
		fileManagerFactory.destroyState(instanceName);
	}

	@Override
	public boolean containsTreeNodeRecords() {
		return Stream.of(NOT_VISITED_TREE_NODES, MUTABLE_TREE_NODES, IMMUTABLE_TREE_NODES)
				.flatMap(fileManager::getRecords)
				.findAny()
				.isPresent();
	}
}
