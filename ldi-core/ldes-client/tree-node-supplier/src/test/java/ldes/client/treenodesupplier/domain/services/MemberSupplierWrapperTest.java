package ldes.client.treenodesupplier.domain.services;

import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MemberSupplierWrapperTest {
	private MemberSupplierWrapper wrapper;


	@Test
	void given_ShouldBeWrapped_when_Wrap_then_ReturnWrappedMemberSupplier() {
		final MemberSupplier base = mock();
		wrapper = new TestMemberSupplierWrapper(true, mock());

		final var result = wrapper.wrapMemberSupplier(base);

		assertThat(result).isNotSameAs(base);
	}

	@Test
	void given_ShouldNotBeWrapped_when_Wrap_then_ReturnBaseMemberSupplier() {
		final MemberSupplier base = mock();
		wrapper = new TestMemberSupplierWrapper(false, mock());

		final var result = wrapper.wrapMemberSupplier(base);

		assertThat(result).isSameAs(base);
	}

	private static class TestMemberSupplierWrapper extends MemberSupplierWrapper {
		private final boolean shouldBeWrapped;
		private final MemberSupplier nextMemberSupplier;

		private TestMemberSupplierWrapper(boolean shouldBeWrapped, MemberSupplier nextMemberSupplier) {
			this.shouldBeWrapped = shouldBeWrapped;
			this.nextMemberSupplier = nextMemberSupplier;
		}

		@Override
		protected boolean shouldBeWrapped() {
			return shouldBeWrapped;
		}

		@Override
		protected MemberSupplier createWrappedMemberSupplier(MemberSupplier memberSupplier) {
			return nextMemberSupplier;
		}
	}
}