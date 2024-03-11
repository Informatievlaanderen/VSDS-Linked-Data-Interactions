package ldes.client.treenodesupplier;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;

public class ExactlyOnceFilterMemberSupplier implements MemberSupplier {
    private final MemberSupplier memberSupplier;
    private final ExactlyOnceFilter filter;
    private final boolean keepState;

    public ExactlyOnceFilterMemberSupplier(MemberSupplier memberSupplier, ExactlyOnceFilter filter, boolean keepState) {
        this.memberSupplier = memberSupplier;
        this.filter = filter;
        this.keepState = keepState;
    }

    @Override
    public SuppliedMember get() {
        SuppliedMember member = memberSupplier.get();
        while(!filter.allowed(member.getId())) {
            member = memberSupplier.get();
        }
        filter.addId(member.getId());
        return member;
    }

    @Override
    public void init() {
        memberSupplier.init();
    }

    @Override
    public void destroyState() {
        if (!keepState) {
            filter.destroyState();
            memberSupplier.destroyState();
        }
    }
}
