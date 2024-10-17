package ldes.client.treenodesupplier.domain.services;

import ldes.client.treenodesupplier.membersuppliers.FilteredMemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplierImpl;
import ldes.client.treenodesupplier.membersuppliers.VersionMaterialisedMemberSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberSupplierWrappersTest {
	private final MemberSupplier baseMemberSupplier = new MemberSupplierImpl(mock(), false);
	private final MemberSupplier firstWrapped = new FilteredMemberSupplier(baseMemberSupplier, mock());
	private final MemberSupplier secondWrapped = new VersionMaterialisedMemberSupplier(firstWrapped, mock());
	@Mock
	private MemberSupplierWrapper mockedMemberSupplierWrapper;

	private MemberSupplierWrappers wrappers;

	@BeforeEach
	void setUp() {
		wrappers = new MemberSupplierWrappers(List.of(
				mockedMemberSupplierWrapper,
				mockedMemberSupplierWrapper,
				mockedMemberSupplierWrapper
		));
	}

	@Test
	void name() {
		when(mockedMemberSupplierWrapper.wrapMemberSupplier(any()))
				.thenReturn(firstWrapped)
				.thenReturn(firstWrapped)
				.thenReturn(secondWrapped);

		final MemberSupplier result = wrappers.wrapMemberSupplier(baseMemberSupplier);

		assertThat(result).isSameAs(secondWrapped);
		verify(mockedMemberSupplierWrapper).wrapMemberSupplier(baseMemberSupplier);
		verify(mockedMemberSupplierWrapper, times(2)).wrapMemberSupplier(firstWrapped);
		verify(mockedMemberSupplierWrapper, never()).wrapMemberSupplier(secondWrapped);
	}

}