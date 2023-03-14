package ldes.client.treenodefetcher.domain.valueobjects;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MutabilityStatusTest {

	@Test
	void when_HeaderIsImmutable_MutabilityStatusRepresentsImmutableResponse() {
		MutabilityStatus mutabilityStatus = MutabilityStatus
				.ofHeader(Optional.of("public, max-age=32536000, immutable"));

		assertFalse(mutabilityStatus.isMutable());
		assertTrue(mutabilityStatus.getEarliestNextVisit().isAfter(LocalDateTime.now().plusYears(1)));
	}

	@Test
	void when_HeaderIsMutable_MutabilityStatusRepresentsMutableResponse() {
		MutabilityStatus mutabilityStatus = MutabilityStatus.ofHeader(Optional.of("max-age=60"));

		assertTrue(mutabilityStatus.isMutable());
		assertTrue(mutabilityStatus.getEarliestNextVisit().isBefore(LocalDateTime.now().plusYears(61)));
		assertTrue(mutabilityStatus.getEarliestNextVisit().isAfter(LocalDateTime.now().plusSeconds(59)));

	}

}