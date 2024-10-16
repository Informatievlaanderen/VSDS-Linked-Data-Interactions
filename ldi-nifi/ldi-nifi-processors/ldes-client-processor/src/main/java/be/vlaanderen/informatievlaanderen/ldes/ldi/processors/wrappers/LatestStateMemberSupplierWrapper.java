package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.wrappers;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.StatePersistenceFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties;
import ldes.client.eventstreamproperties.valueobjects.EventStreamProperties;
import ldes.client.treenodesupplier.domain.services.MemberSupplierWrapper;
import ldes.client.treenodesupplier.filters.LatestStateFilter;
import ldes.client.treenodesupplier.filters.MemberFilter;
import ldes.client.treenodesupplier.membersuppliers.FilteredMemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import ldes.client.treenodesupplier.repository.MemberVersionRepository;
import org.apache.nifi.processor.ProcessContext;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties.useLatestStateFilter;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties.useVersionMaterialisation;

public class LatestStateMemberSupplierWrapper extends MemberSupplierWrapper {
	private final ProcessContext context;
	private final EventStreamProperties eventStreamProperties;

	public LatestStateMemberSupplierWrapper(ProcessContext context, EventStreamProperties eventStreamProperties) {
		this.context = context;
		this.eventStreamProperties = eventStreamProperties;
	}

	@Override
	protected boolean shouldBeWrapped() {
		return useVersionMaterialisation(context) && useLatestStateFilter(context);
	}

	@Override
	protected MemberSupplier createWrappedMemberSupplier(MemberSupplier memberSupplier) {
		return new FilteredMemberSupplier(memberSupplier, createMemberFilter());
	}

	private MemberFilter createMemberFilter() {
		final MemberVersionRepository memberVersionRepository = new StatePersistenceFactory()
				.getStatePersistence(context)
				.getMemberVersionRepository();
		final boolean keepState = PersistenceProperties.stateKept(context);
		return new LatestStateFilter(memberVersionRepository, keepState, eventStreamProperties.getTimestampPath(), eventStreamProperties.getVersionOfPath());
	}
}
