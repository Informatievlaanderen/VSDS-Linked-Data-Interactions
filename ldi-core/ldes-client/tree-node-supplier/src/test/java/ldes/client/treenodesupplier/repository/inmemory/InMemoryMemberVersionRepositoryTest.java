package ldes.client.treenodesupplier.repository.inmemory;

import ldes.client.treenodesupplier.domain.entities.MemberVersionRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryMemberVersionRepositoryTest {
	public static final String IS_VERSION_OF = "isVersionOf";
	private InMemoryMemberVersionRepository repository;
	private final LocalDateTime timestamp = LocalDateTime.of(2024, 4, 29, 0, 0);
	private final MemberVersionRecord memberVersionToTest = new MemberVersionRecord(IS_VERSION_OF, timestamp);

	@BeforeEach
	void setUp() {
		repository = new InMemoryMemberVersionRepository();
	}

	@Test
	void given_emptyRepository_when_isVersionAfterTimestamp_then_ReturnTrue() {
		final boolean actual = repository.isVersionAfterTimestamp(memberVersionToTest);

		assertThat(actual).isTrue();
	}

	@ParameterizedTest
	@ArgumentsSource(TimestampProvider.class)
	void given_NonEmptyRepository_test_IsVersionAfterTimestamp(LocalDateTime timestamp, boolean expectedIsAfter) {
		final MemberVersionRecord insertedMember = new MemberVersionRecord(IS_VERSION_OF, timestamp);
		repository.addMemberVersion(insertedMember);

		final boolean actual = repository.isVersionAfterTimestamp(memberVersionToTest);

		assertThat(actual).isEqualTo(expectedIsAfter);
	}

	static class TimestampProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of(LocalDateTime.of(2024, 3, 28, 0, 0), true),
					Arguments.of(LocalDateTime.of(2024, 4, 29, 0, 0), false),
					Arguments.of(LocalDateTime.of(2024, 5, 30, 0, 0), false)
			);
		}
	}
}