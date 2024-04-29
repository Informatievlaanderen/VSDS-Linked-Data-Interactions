package ldes.client.treenodesupplier.filters;

import ldes.client.treenodesupplier.repository.MemberIdRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ExactlyOnceFilterTest {
	@Mock
	private MemberIdRepository memberIdRepository;

	@Test
	void given_KeepStateIsFalse_when_DestroyState_then_DestroyStateFromRepo() {
		final ExactlyOnceFilter exactlyOnceFilter = new ExactlyOnceFilter(memberIdRepository, false);
		exactlyOnceFilter.destroyState();

		verify(memberIdRepository).destroyState();
	}

	@Test
	void given_KeepStateIsTrue_when_DestroyState_then_DoeNotDestroyStateFromRepo() {
		final ExactlyOnceFilter exactlyOnceFilter = new ExactlyOnceFilter(memberIdRepository, true);

		exactlyOnceFilter.destroyState();

		verifyNoInteractions(memberIdRepository);
	}
}