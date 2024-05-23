package be.vlaanderen.informatievlaanderen.ldes.ldi.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class HashedStateMemberTest {
	private static final String MEMBER_ID = "unique-member-id";
	private static final String MEMBER_HASH = "unique-member-hash";
	private static final HashedStateMember UNDER_TEST = new HashedStateMember(MEMBER_ID, MEMBER_HASH);

	private static Stream<Arguments> provideInEqualHashedStateMembers() {
		return Stream.of(
				new HashedStateMember("other-id", MEMBER_HASH),
				new HashedStateMember("other-id", "other-hash"),
				"other-type",
				null
		).map(Arguments::of);
	}

	@Test
	void test_equality() {
		final HashedStateMember memberWithSameProperties = new HashedStateMember(MEMBER_ID, MEMBER_HASH);
		final HashedStateMember memberWithOtherHash = new HashedStateMember(MEMBER_ID, "other-hash");

		assertThat(UNDER_TEST)
				.isEqualTo(UNDER_TEST)
				.isEqualTo(memberWithSameProperties)
				.isEqualTo(memberWithOtherHash);

		assertThat(memberWithSameProperties)
				.isEqualTo(UNDER_TEST)
				.isEqualTo(memberWithOtherHash);

		assertThat(UNDER_TEST.hashCode())
				.isEqualTo(memberWithSameProperties.hashCode())
				.isEqualTo(memberWithOtherHash.hashCode());
	}

	@ParameterizedTest
	@MethodSource("provideInEqualHashedStateMembers")
	void test_inequality(Object other) {
		assertThat(other).isNotEqualTo(UNDER_TEST);

		if(other != null) {
			assertThat(other.hashCode()).isNotEqualTo(UNDER_TEST.hashCode());
		}
	}
}