package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.wrappers;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties;
import ldes.client.treenodesupplier.domain.services.MemberSupplierWrapper;
import ldes.client.treenodesupplier.filters.ExactlyOnceFilter;
import ldes.client.treenodesupplier.filters.MemberFilter;
import ldes.client.treenodesupplier.membersuppliers.FilteredMemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import ldes.client.treenodesupplier.repository.MemberIdRepository;
import org.apache.nifi.processor.ProcessContext;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties.useExactlyOnceFilter;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties.useVersionMaterialisation;

public class ExactlyOnceMemberSupplierWrapper extends MemberSupplierWrapper {
	private final ProcessContext context;
	private final MemberIdRepository memberIdRepository;

	public ExactlyOnceMemberSupplierWrapper(ProcessContext context, MemberIdRepository memberIdRepository) {
		this.context = context;
		this.memberIdRepository = memberIdRepository;
	}

	@Override
	protected boolean shouldBeWrapped() {
		return !useVersionMaterialisation(context) && useExactlyOnceFilter(context);
	}

	@Override
	protected MemberSupplier createWrappedMemberSupplier(MemberSupplier memberSupplier) {
		return new FilteredMemberSupplier(memberSupplier, createMemberFilter());
	}

	private MemberFilter createMemberFilter() {
		final boolean keepState = PersistenceProperties.stateKept(context);
		return new ExactlyOnceFilter(memberIdRepository, keepState);
	}
}
