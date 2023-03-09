package ldes.client.treenodesupplier;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;

import java.util.function.Supplier;

public class MemberSupplier implements Supplier<MemberRecord> {

	private final Processor processor;

	public MemberSupplier(Processor processor) {
		this.processor = processor;
	}

	@Override
	public MemberRecord get() {
		return processor.getMember();
	}
}
