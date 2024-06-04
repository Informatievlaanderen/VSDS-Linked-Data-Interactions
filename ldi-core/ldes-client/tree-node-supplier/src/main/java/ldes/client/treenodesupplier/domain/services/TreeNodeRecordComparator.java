package ldes.client.treenodesupplier.domain.services;

import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Comparator;

public class TreeNodeRecordComparator implements Comparator<TreeNodeRecord> {
	/**
	 * Compares two TreeNodeRecords by {@link TreeNodeRecord#getEarliestNextVisit()}
	 * @param o1 the first object to be compared.
	 * @param o2 the second object to be compared.
	 * @return integer comparing result
	 * @see java.time.LocalDateTime#compareTo(ChronoLocalDateTime)
	 */
	@Override
	public int compare(TreeNodeRecord o1, TreeNodeRecord o2) {
		return o1.getEarliestNextVisit().compareTo(o2.getEarliestNextVisit());
	}
}
