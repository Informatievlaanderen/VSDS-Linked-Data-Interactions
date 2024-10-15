package be.vlaanderen.informatievlaanderen.ldes.ldio.config.wrappers;

import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;

public abstract class MemberSupplierWrapper {
	public final MemberSupplier wrapMemberSupplier(MemberSupplier memberSupplier) {
		return shouldBeWrapped()
				? createWrappedMemberSupplier(memberSupplier)
				: memberSupplier;
	}

	protected abstract boolean shouldBeWrapped();

	protected abstract MemberSupplier createWrappedMemberSupplier(MemberSupplier memberSupplier);
}
