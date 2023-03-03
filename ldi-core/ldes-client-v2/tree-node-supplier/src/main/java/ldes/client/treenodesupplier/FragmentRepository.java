package ldes.client.treenodesupplier;

import java.util.ArrayList;
import java.util.List;

public class FragmentRepository {

	List<String> unprocessed = new ArrayList<>();
	List<String> processed = new ArrayList<>();

	public void processedTreeNode(String processedTreenode) {
		processed.add(processedTreenode);
	}

	public boolean isProcessed(String relation) {
		return processed.contains(relation);
	}

	public void addUnProcessedTreeNode(String s) {
		unprocessed.add(s);
	}

	public String getUnprocessedTreeNode() {
		return unprocessed.remove(0);
	}
}
