package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.wrappers.ExactlyOnceMemberSupplierWrapper;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import ldes.client.treenodesupplier.membersuppliers.FilteredMemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExactlyOnceMemberSupplierWrapperTest {
	@Mock
	private ComponentProperties componentProperties;
	@Mock
	private LdioLdesClientProperties ldioLdesClientProperties;
	@Mock
	private MemberSupplier baseSupplier;
	@InjectMocks
	private ExactlyOnceMemberSupplierWrapper exactlyOnceMemberSupplierWrapper;

	@Test
	void given_ExactlyOnceEnabled_when_wrap_then_ReturnFilteredMemberSupplier() {
		when(ldioLdesClientProperties.isExactlyOnceEnabled()).thenReturn(true);
		when(ldioLdesClientProperties.getProperties()).thenReturn(componentProperties);

		final MemberSupplier memberSupplier = exactlyOnceMemberSupplierWrapper.wrapMemberSupplier(baseSupplier);

		assertThat(memberSupplier).isInstanceOf(FilteredMemberSupplier.class);
	}

	@Test
	void given_ExactlyOnceDisabled_when_wrap_then_ReturnBaseMemberSupplier() {
		when(ldioLdesClientProperties.isExactlyOnceEnabled()).thenReturn(false);

		final MemberSupplier memberSupplier = exactlyOnceMemberSupplierWrapper.wrapMemberSupplier(baseSupplier);

		assertThat(memberSupplier).isSameAs(baseSupplier);
	}

}