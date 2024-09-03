package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMember;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.HashedStateMemberRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeDetectionFilterTest {
	@Mock
	private HashedStateMemberRepository hashedStateMemberRepository;

	private ChangeDetectionFilter changeDetectionFilter;

	@BeforeEach
	void setUp() {
		changeDetectionFilter = new ChangeDetectionFilter(hashedStateMemberRepository, false);
	}

	@Test
	void when_FilterValidStateObject_then_ThrowNoException() {
		final Model validStateObject = supplyStateObject();

		assertThatNoException().isThrownBy(() -> changeDetectionFilter.transform(validStateObject));
	}

	@ParameterizedTest
	@MethodSource("supplyInvalidTriples")
	void when_FilterInvalidStateObjects_then_ThrowException(String triples) {
		final Model invalidStateObject = RDFParser.fromString(triples).lang(Lang.NT).toModel();

		assertThatThrownBy(() -> changeDetectionFilter.transform(invalidStateObject))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("State object must contain exactly one named node");
	}

	@Test
	void given_RepositoryContainingMember_when_FilterModel_then_ReturnEmptyModel() {
		when(hashedStateMemberRepository.containsHashedStateMember(any())).thenReturn(true);

		final Model filteredModel = changeDetectionFilter.transform(supplyStateObject());

		assertThat(filteredModel).matches(Model::isEmpty);
	}

	@Test
	void given_EmptyRepository_when_FilterModel_then_ReturnExactModel() {
		final Model modelToFilter = supplyStateObject();
		final HashedStateMember expectedHashedStateMember = argThat(member -> member.memberId().equals("http://test-data/mobility-hindrance/1"));
		when(hashedStateMemberRepository.containsHashedStateMember(expectedHashedStateMember)).thenReturn(false);

		final Model filteredModel = changeDetectionFilter.transform(modelToFilter);

		assertThat(filteredModel).isSameAs(modelToFilter);
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void given_KeepState_when_DestroyFilterState_then_VerifyRepositoryDestroy(boolean keepState) {
		final int expectedDestroyRepositoryInvocations = keepState ? 0 : 1;
		final var filter = new ChangeDetectionFilter(hashedStateMemberRepository, keepState);

		filter.destroyState();

		verify(hashedStateMemberRepository, times(expectedDestroyRepositoryInvocations)).destroyState();
	}


	private static Stream<Arguments> supplyInvalidTriples() {
		final String twoStateObjects = """
				<http://test-data/mobility-hindrance/1/2> <http://purl.org/dc/terms/isVersionOf> <http://test-data/mobility-hindrance/1> .
				<http://test-data/mobility-hindrance/1/2> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder> .
				<http://test-data/mobility-hindrance/1/2> <http://purl.org/dc/terms/created> "2023-04-06T09:58:15.867Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
				<http://test-data/mobility-hindrance/1/3> <http://purl.org/dc/terms/isVersionOf> <http://test-data/mobility-hindrance/1> .
				<http://test-data/mobility-hindrance/1/3> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder> .
				<http://test-data/mobility-hindrance/1/3> <http://purl.org/dc/terms/created> "2023-04-06T09:58:15.867Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
				""";
		return Stream.of(
				Arguments.of(""),
				Arguments.of(twoStateObjects)
		);
	}

	private static Model supplyStateObject() {
		return RDFParser.source("members/state-member.nq").toModel();
	}
}