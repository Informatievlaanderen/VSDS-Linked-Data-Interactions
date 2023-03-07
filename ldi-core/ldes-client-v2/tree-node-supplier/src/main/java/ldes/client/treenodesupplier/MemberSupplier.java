package ldes.client.treenodesupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import ldes.client.treenodefetcher.domain.entities.TreeMember;

import java.util.function.Supplier;

public class MemberSupplier implements Supplier<TreeMember>, LdiInput {

	private final Processor processor;

	public MemberSupplier(Processor processor) {
		this.processor = processor;
	}

	@Override
	public TreeMember get() {
		return processor.getMember();
	}
}
