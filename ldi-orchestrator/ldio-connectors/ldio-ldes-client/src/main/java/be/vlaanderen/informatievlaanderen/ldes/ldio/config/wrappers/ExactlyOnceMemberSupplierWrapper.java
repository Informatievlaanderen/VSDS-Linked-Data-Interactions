package be.vlaanderen.informatievlaanderen.ldes.ldio.config.wrappers;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdesClientRepositoriesFactory;
import ldes.client.treenodesupplier.domain.services.MemberSupplierWrapper;
import ldes.client.treenodesupplier.domain.valueobject.LdesClientRepositories;
import ldes.client.treenodesupplier.filters.ExactlyOnceFilter;
import ldes.client.treenodesupplier.filters.MemberFilter;
import ldes.client.treenodesupplier.membersuppliers.FilteredMemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;

public class ExactlyOnceMemberSupplierWrapper extends MemberSupplierWrapper {
	private final LdioLdesClientProperties properties;

	public ExactlyOnceMemberSupplierWrapper(LdioLdesClientProperties properties) {
		this.properties = properties;
	}

	@Override
	public boolean shouldBeWrapped() {
		return properties.isExactlyOnceEnabled();
	}

	@Override
	protected MemberSupplier createWrappedMemberSupplier(MemberSupplier memberSupplier) {
		return new FilteredMemberSupplier(memberSupplier, createExactlyOnceFilter());
	}

	private MemberFilter createExactlyOnceFilter() {
		final LdesClientRepositories ldesClientRepositories = new LdesClientRepositoriesFactory().getStatePersistence(properties.getProperties());
		return new ExactlyOnceFilter(ldesClientRepositories.memberIdRepository(), properties.isKeepStateEnabled());
	}
}
