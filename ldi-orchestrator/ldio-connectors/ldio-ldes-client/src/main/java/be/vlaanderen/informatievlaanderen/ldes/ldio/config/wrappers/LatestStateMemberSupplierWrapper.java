package be.vlaanderen.informatievlaanderen.ldes.ldio.config.wrappers;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdesClientRepositoriesFactory;
import ldes.client.eventstreamproperties.valueobjects.EventStreamProperties;
import ldes.client.treenodesupplier.domain.services.MemberSupplierWrapper;
import ldes.client.treenodesupplier.domain.valueobject.LdesClientRepositories;
import ldes.client.treenodesupplier.filters.LatestStateFilter;
import ldes.client.treenodesupplier.filters.MemberFilter;
import ldes.client.treenodesupplier.membersuppliers.FilteredMemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;

public class LatestStateMemberSupplierWrapper extends MemberSupplierWrapper {
	private final EventStreamProperties eventStreamProperties;
	private final LdioLdesClientProperties ldioLdesClientProperties;

	public LatestStateMemberSupplierWrapper(EventStreamProperties eventStreamProperties, LdioLdesClientProperties ldioLdesClientProperties) {
		this.eventStreamProperties = eventStreamProperties;
		this.ldioLdesClientProperties = ldioLdesClientProperties;
	}

	@Override
	public boolean shouldBeWrapped() {
		return ldioLdesClientProperties.isVersionMaterialisationEnabled() && ldioLdesClientProperties.isLatestStateEnabled();
	}

	@Override
	protected MemberSupplier createWrappedMemberSupplier(MemberSupplier memberSupplier) {
		return new FilteredMemberSupplier(memberSupplier, createLatestStateFilter());
	}

	private MemberFilter createLatestStateFilter() {
		final LdesClientRepositories ldesClientRepositories = LdesClientRepositoriesFactory.getLdesClientRepositories(ldioLdesClientProperties.getProperties());
		return new LatestStateFilter(ldesClientRepositories.memberVersionRepository(), ldioLdesClientProperties.isKeepStateEnabled(), eventStreamProperties.getTimestampPath(), eventStreamProperties.getVersionOfPath());
	}
}
