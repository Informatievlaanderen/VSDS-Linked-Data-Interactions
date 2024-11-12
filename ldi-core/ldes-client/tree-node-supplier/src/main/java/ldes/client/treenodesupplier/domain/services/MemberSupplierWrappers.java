package ldes.client.treenodesupplier.domain.services;

import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;

import java.util.List;

public class MemberSupplierWrappers {
	private final List<MemberSupplierWrapper> wrappers;

	public MemberSupplierWrappers(List<MemberSupplierWrapper> wrappers) {
		this.wrappers = wrappers;
	}

	public MemberSupplier wrapMemberSupplier(MemberSupplier memberSupplier) {
		for (MemberSupplierWrapper wrapper : wrappers) {
			memberSupplier = wrapper.wrapMemberSupplier(memberSupplier);
		}
		return memberSupplier;
	}

	public interface Builder {
		MemberSupplierWrappers build();
	}
}
