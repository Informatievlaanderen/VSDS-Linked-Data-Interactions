package ldes.client.treenodesupplier;

import ldes.client.treenodesupplier.domain.entities.SuppliedMember;

import java.util.function.Supplier;

public class MemberSupplier implements Supplier<SuppliedMember> {

	private final TreeNodeProcessor treeNodeProcessor;

	public MemberSupplier(TreeNodeProcessor treeNodeProcessor) {
		this.treeNodeProcessor = treeNodeProcessor;
	}

	@Override
	public SuppliedMember get() {
		return treeNodeProcessor.getMember();
	}
}
