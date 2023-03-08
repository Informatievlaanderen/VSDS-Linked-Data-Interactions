package ldes.client.treenodesupplier;

import ldes.client.treenodefetcher.domain.entities.TreeMember;

import java.util.function.Supplier;

public class MemberSupplier implements Supplier<TreeMember> {

	private final Processor processor;

	public MemberSupplier(Processor processor) {
		this.processor = processor;
	}

	@Override
	public TreeMember get() {
		return processor.getMember();
	}
}
