package ldes.client.treenodesupplier;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;

public class MemberSupplierImpl implements MemberSupplier {

    private final TreeNodeProcessor treeNodeProcessor;
    private final boolean keepState;

    public MemberSupplierImpl(TreeNodeProcessor treeNodeProcessor, boolean keepState) {
        this.treeNodeProcessor = treeNodeProcessor;
        this.keepState = keepState;
        Runtime.getRuntime().addShutdownHook(new Thread(this::destroyState));
    }

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
