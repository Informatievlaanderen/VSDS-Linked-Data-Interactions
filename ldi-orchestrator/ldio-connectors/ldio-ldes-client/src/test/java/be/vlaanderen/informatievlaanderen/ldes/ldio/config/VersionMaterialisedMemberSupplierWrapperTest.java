package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.wrappers.VersionMaterialisedMemberSupplierWrapper;
import ldes.client.eventstreamproperties.valueobjects.EventStreamProperties;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.VersionMaterialisedMemberSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VersionMaterialisedMemberSupplierWrapperTest {
	@Mock
	private LdioLdesClientProperties ldioLdesClientProperties;
	@Mock
	private MemberSupplier baseSupplier;
	private VersionMaterialisedMemberSupplierWrapper versionMaterialisedMemberSupplierWrapper;

	@BeforeEach
	void setUp() {
		final EventStreamProperties eventStreamProperties = new EventStreamProperties("test", "test", "test", "test");
		versionMaterialisedMemberSupplierWrapper = new VersionMaterialisedMemberSupplierWrapper(eventStreamProperties, ldioLdesClientProperties);
	}

	@Test
	void given_VersionMaterialisationEnabled_when_wrap_then_ReturnVersionMaterialisedMemberSupplier() {
		when(ldioLdesClientProperties.isVersionMaterialisationEnabled()).thenReturn(true);

		final MemberSupplier memberSupplier = versionMaterialisedMemberSupplierWrapper.wrapMemberSupplier(baseSupplier);

		assertThat(memberSupplier).isInstanceOf(VersionMaterialisedMemberSupplier.class);
	}

	@Test
	void given_VersionMaterialisationDisabled_when_wrap_then_ReturnBaseMemberSupplier() {
		when(ldioLdesClientProperties.isVersionMaterialisationEnabled()).thenReturn(false);

		final MemberSupplier memberSupplier = versionMaterialisedMemberSupplierWrapper.wrapMemberSupplier(baseSupplier);

		assertThat(memberSupplier).isSameAs(baseSupplier);
	}

}