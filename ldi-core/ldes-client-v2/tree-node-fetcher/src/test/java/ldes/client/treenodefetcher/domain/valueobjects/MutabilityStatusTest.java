package ldes.client.treenodefetcher.domain.valueobjects;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MutabilityStatusTest {

	@Test
	void when_HeaderIsImmutable_MutabilityStatusRepresentsImmutableResponse() {
		MutabilityStatus mutabilityStatus = MutabilityStatus
				.ofHeader("public, max-age=32536000, immutable");

		assertFalse(mutabilityStatus.isMutable());
		assertTrue(mutabilityStatus.getEarliestNextVisit().isAfter(LocalDateTime.now().plusYears(1)));
	}

	@Test
	void when_HeaderIsMutable_MutabilityStatusRepresentsMutableResponse() {
		MutabilityStatus mutabilityStatus = MutabilityStatus.ofHeader("max-age=60");

		assertTrue(mutabilityStatus.isMutable());
		assertTrue(mutabilityStatus.getEarliestNextVisit().isBefore(LocalDateTime.now().plusYears(61)));
		assertTrue(mutabilityStatus.getEarliestNextVisit().isAfter(LocalDateTime.now().plusSeconds(59)));

	}

	@Test
	void when_empty_MutabilityStatusRepresentsMutableResponse() {
		MutabilityStatus mutabilityStatus = MutabilityStatus.empty();

		assertTrue(mutabilityStatus.isMutable());
		assertTrue(mutabilityStatus.getEarliestNextVisit().isBefore(LocalDateTime.now().plusYears(61)));
		assertTrue(mutabilityStatus.getEarliestNextVisit().isAfter(LocalDateTime.now().plusSeconds(59)));
	}
}