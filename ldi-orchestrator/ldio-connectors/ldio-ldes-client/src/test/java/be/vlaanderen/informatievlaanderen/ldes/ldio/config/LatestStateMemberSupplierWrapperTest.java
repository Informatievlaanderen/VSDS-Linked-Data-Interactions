package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.wrappers.LatestStateMemberSupplierWrapper;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import ldes.client.eventstreamproperties.valueobjects.EventStreamProperties;
import ldes.client.treenodesupplier.membersuppliers.FilteredMemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LatestStateMemberSupplierWrapperTest {
	@Mock
	private ComponentProperties componentProperties;
	@Mock
	private LdioLdesClientProperties ldioLdesClientProperties;
	@Mock
	private MemberSupplier baseSupplier;
	private LatestStateMemberSupplierWrapper latestStateMemberSupplierWrapper;

	@BeforeEach
	void setUp() {
		final EventStreamProperties eventStreamProperties = new EventStreamProperties("test", "test", "test");
		latestStateMemberSupplierWrapper = new LatestStateMemberSupplierWrapper(eventStreamProperties, ldioLdesClientProperties);
	}

	@Test
	void given_LatestStateEnabled_when_wrap_then_ReturnFilteredMemberSupplier() {
		when(ldioLdesClientProperties.isLatestStateEnabled()).thenReturn(true);
		when(ldioLdesClientProperties.isVersionMaterialisationEnabled()).thenReturn(true);
		when(ldioLdesClientProperties.getProperties()).thenReturn(componentProperties);

		final MemberSupplier memberSupplier = latestStateMemberSupplierWrapper.wrapMemberSupplier(baseSupplier);

		assertThat(memberSupplier).isInstanceOf(FilteredMemberSupplier.class);
	}

	@Test
	void given_VersionMaterialisationDisabled_when_wrap_then_ReturnBaseSupplier() {
		when(ldioLdesClientProperties.isVersionMaterialisationEnabled()).thenReturn(false);
		final MemberSupplier memberSupplier = latestStateMemberSupplierWrapper.wrapMemberSupplier(baseSupplier);

		assertThat(memberSupplier).isSameAs(baseSupplier);
	}

	@Test
	void given_LatestStateDisabled_when_wrap_then_ReturnBaseSupplier() {
		when(ldioLdesClientProperties.isVersionMaterialisationEnabled()).thenReturn(true);
		when(ldioLdesClientProperties.isLatestStateEnabled()).thenReturn(false);
		final MemberSupplier memberSupplier = latestStateMemberSupplierWrapper.wrapMemberSupplier(baseSupplier);

		assertThat(memberSupplier).isSameAs(baseSupplier);
	}



}