package ldes.client.treenodesupplier;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;

import java.util.function.Supplier;

public class MemberSupplier implements Supplier<SuppliedMember> {

	private final TreeNodeProcessor treeNodeProcessor;
	private final boolean keepState;

	public MemberSupplier(TreeNodeProcessor treeNodeProcessor, boolean keepState) {
		this.treeNodeProcessor = treeNodeProcessor;
		this.keepState = keepState;
		Runtime.getRuntime().addShutdownHook(new Thread(this::destroyState));
	}

	@Override
	public SuppliedMember get() {
		return treeNodeProcessor.getMember();
	}

	public void destroyState() {
		if (!keepState) {
			treeNodeProcessor.destroyState();
		}
	}
}
