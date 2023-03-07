package ldes.client.treenodesupplier.domain.services;

import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;

import java.util.Comparator;

public class TreeNodeRecordComparator implements Comparator<TreeNodeRecord> {
	@Override
	public int compare(TreeNodeRecord o1, TreeNodeRecord o2) {
		return o1.getEarliestNextVisit().compareTo(o2.getEarliestNextVisit());
	}
}
