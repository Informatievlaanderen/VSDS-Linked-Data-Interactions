package ldes.client.treenodesupplier.membersuppliers;

import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;

/**
 * Base implementation of the {@link MemberSupplier}
 */
public class MemberSupplierImpl implements MemberSupplier {

	private final TreeNodeProcessor treeNodeProcessor;
	private final boolean keepState;

	public MemberSupplierImpl(TreeNodeProcessor treeNodeProcessor, boolean keepState) {
		this.treeNodeProcessor = treeNodeProcessor;
		this.keepState = keepState;
		Runtime.getRuntime().addShutdownHook(new Thread(this::destroyState));
	}

	/**
	 * Base implementation that returns the fetched TreeNode member
	 *
	 * @return The plain fetched TreeNode member
	 */
	@Override
	public SuppliedMember get() {
		return treeNodeProcessor.getMember();
	}

	@Override
	public void destroyState() {
		if (!keepState) {
			treeNodeProcessor.destroyState();
		}
	}

	@Override
	public void init() {
		treeNodeProcessor.init();
	}
}
