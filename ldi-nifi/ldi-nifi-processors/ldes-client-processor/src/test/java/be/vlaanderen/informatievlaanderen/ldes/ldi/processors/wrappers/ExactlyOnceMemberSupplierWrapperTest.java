package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.wrappers;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.wrappers.testutils.TestProcessContext;
import ldes.client.treenodesupplier.membersuppliers.FilteredMemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplierImpl;
import org.apache.nifi.processor.ProcessContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExactlyOnceMemberSupplierWrapperTest {
	private final MemberSupplier baseSupplier = new MemberSupplierImpl(null, false);
	private ExactlyOnceMemberSupplierWrapper wrapper;

	@Test
	void given_ExactlyOnceEnabled_when_wrap_then_ReturnFilteredMemberSupplier() {
		final ProcessContext context = new TestProcessContext(true);
		wrapper = new ExactlyOnceMemberSupplierWrapper(context);

		final MemberSupplier memberSupplier = wrapper.wrapMemberSupplier(baseSupplier);

		assertThat(memberSupplier).isInstanceOf(FilteredMemberSupplier.class);
	}

	@Test
	void given_ExactlyOnceAndVersionMaterialisationEnabled_when_wrap_then_ReturnBaseMemberSupplier() {
		final ProcessContext context = new TestProcessContext(true, true, true);
		wrapper = new ExactlyOnceMemberSupplierWrapper(context);

		final MemberSupplier memberSupplier = wrapper.wrapMemberSupplier(baseSupplier);

		assertThat(memberSupplier).isSameAs(baseSupplier);
	}

	@Test
	void given_ExactlyOnceDisabled_when_wrap_then_ReturnBaseMemberSupplier() {
		final ProcessContext context = new TestProcessContext(false);
		wrapper = new ExactlyOnceMemberSupplierWrapper(context);

		final MemberSupplier memberSupplier = wrapper.wrapMemberSupplier(baseSupplier);

		assertThat(memberSupplier).isSameAs(baseSupplier);
	}
}