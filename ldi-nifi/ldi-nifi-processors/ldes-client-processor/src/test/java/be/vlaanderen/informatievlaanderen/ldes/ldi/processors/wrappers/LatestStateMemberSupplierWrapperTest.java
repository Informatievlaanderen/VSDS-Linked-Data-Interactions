package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.wrappers;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.wrappers.testutils.TestProcessContext;
import ldes.client.eventstreamproperties.valueobjects.EventStreamProperties;
import ldes.client.treenodesupplier.membersuppliers.FilteredMemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplierImpl;
import org.apache.nifi.processor.ProcessContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LatestStateMemberSupplierWrapperTest {
	private final MemberSupplier baseSupplier = new MemberSupplierImpl(null, false);
	private final EventStreamProperties eventStreamProperties = new EventStreamProperties("test", "test", "test", "test");
	private LatestStateMemberSupplierWrapper wrapper;

	@Test
	void given_LatestStateEnabled_when_wrap_then_ReturnFilteredMemberSupplier() {
		final ProcessContext context = new TestProcessContext(true, true);
		wrapper = new LatestStateMemberSupplierWrapper(context, null, eventStreamProperties);

		final MemberSupplier memberSupplier = wrapper.wrapMemberSupplier(baseSupplier);

		assertThat(memberSupplier).isInstanceOf(FilteredMemberSupplier.class);
	}

	@Test
	void given_VersionMaterialisationDisabled_when_wrap_then_ReturnBaseSupplier() {
		final ProcessContext context = new TestProcessContext(false, true);
		wrapper = new LatestStateMemberSupplierWrapper(context, null, eventStreamProperties);

		final MemberSupplier memberSupplier = wrapper.wrapMemberSupplier(baseSupplier);

		assertThat(memberSupplier).isSameAs(baseSupplier);
	}

	@Test
	void given_LatestStateDisabled_when_wrap_then_ReturnBaseSupplier() {
		final ProcessContext context = new TestProcessContext(true, false);
		wrapper = new LatestStateMemberSupplierWrapper(context, null, eventStreamProperties);

		final MemberSupplier memberSupplier = wrapper.wrapMemberSupplier(baseSupplier);

		assertThat(memberSupplier).isSameAs(baseSupplier);
	}
}