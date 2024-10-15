package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.wrappers.VersionMaterialiserMemberSupplierWrapper;
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
class VersionMaterialiserMemberSupplierWrapperTest {
	@Mock
	private LdioLdesClientProperties ldioLdesClientProperties;
	@Mock
	private MemberSupplier baseSupplier;
	private VersionMaterialiserMemberSupplierWrapper versionMaterialiserMemberSupplierWrapper;

	@BeforeEach
	void setUp() {
		final EventStreamProperties eventStreamProperties = new EventStreamProperties("test", "test", "test");
		versionMaterialiserMemberSupplierWrapper = new VersionMaterialiserMemberSupplierWrapper(eventStreamProperties, ldioLdesClientProperties);
	}

	@Test
	void given_ExactlyOnceEnabled_when_wrap_then_ReturnFilteredMemberSupplier() {
		when(ldioLdesClientProperties.isVersionMaterialisationEnabled()).thenReturn(true);

		final MemberSupplier memberSupplier = versionMaterialiserMemberSupplierWrapper.wrapMemberSupplier(baseSupplier);

		assertThat(memberSupplier).isInstanceOf(VersionMaterialisedMemberSupplier.class);
	}

	@Test
	void given_ExactlyOnceDisabled_when_wrap_then_ReturnFilteredMemberSupplier() {
		when(ldioLdesClientProperties.isVersionMaterialisationEnabled()).thenReturn(false);

		final MemberSupplier memberSupplier = versionMaterialiserMemberSupplierWrapper.wrapMemberSupplier(baseSupplier);

		assertThat(memberSupplier).isSameAs(baseSupplier);
	}

}