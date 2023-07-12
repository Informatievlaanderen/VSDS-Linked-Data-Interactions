package ldes.client.treenodesupplier.repository.filebased.mapper;

import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;

import java.time.LocalDateTime;

public class TreeNodeRecordMapper {

	public static final String DELIMITER = ";;;";

	public String fromTreeNodeRecord(TreeNodeRecord treeNodeRecord) {
		return String.join(DELIMITER, treeNodeRecord.getTreeNodeUrl(), treeNodeRecord.getTreeNodeStatus().toString(),
				treeNodeRecord.getEarliestNextVisit().toString());
	}

	public TreeNodeRecord toTreeNodeRecord(String line) {
		String[] parts = line.split(DELIMITER);
		return new TreeNodeRecord(parts[0], TreeNodeStatus.valueOf(parts[1]), LocalDateTime.parse(parts[2]));
	}
}
