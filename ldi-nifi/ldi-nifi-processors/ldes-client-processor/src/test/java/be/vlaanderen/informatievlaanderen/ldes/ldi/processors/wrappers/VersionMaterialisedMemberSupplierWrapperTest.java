package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.wrappers;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.wrappers.testutils.TestProcessContext;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplierImpl;
import ldes.client.treenodesupplier.membersuppliers.VersionMaterialisedMemberSupplier;
import org.apache.nifi.processor.ProcessContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VersionMaterialisedMemberSupplierWrapperTest {
	private final MemberSupplier baseSupplier = new MemberSupplierImpl(null, false);
	private final String versionOfPath = "test";
	private VersionMaterialisedMemberSupplierWrapper versionMaterialisedMemberSupplierWrapper;


	@Test
	void given_VersionMaterialisationEnabled_when_wrap_then_ReturnVersionMaterialisedMemberSupplier() {
		ProcessContext context = new TestProcessContext(true, false);
		versionMaterialisedMemberSupplierWrapper = new VersionMaterialisedMemberSupplierWrapper(context, versionOfPath);
		final MemberSupplier memberSupplier = versionMaterialisedMemberSupplierWrapper.wrapMemberSupplier(baseSupplier);

		assertThat(memberSupplier).isInstanceOf(VersionMaterialisedMemberSupplier.class);
	}

	@Test
	void given_VersionMaterialisationDisabled_when_wrap_then_ReturnBaseMemberSupplier() {
		ProcessContext context = new TestProcessContext(false, false);
		versionMaterialisedMemberSupplierWrapper = new VersionMaterialisedMemberSupplierWrapper(context, versionOfPath);
		final MemberSupplier memberSupplier = versionMaterialisedMemberSupplierWrapper.wrapMemberSupplier(baseSupplier);

		assertThat(memberSupplier).isSameAs(baseSupplier);
	}
}