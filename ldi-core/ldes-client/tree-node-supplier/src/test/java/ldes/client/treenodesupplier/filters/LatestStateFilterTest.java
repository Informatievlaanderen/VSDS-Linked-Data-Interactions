package ldes.client.treenodesupplier.filters;

import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyPathExtractor;
import ldes.client.treenodesupplier.domain.entities.MemberVersionRecord;
import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import ldes.client.treenodesupplier.repository.MemberVersionRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LatestStateFilterTest {
	@Mock
	private MemberVersionRepository memberVersionRepository;
	private final PropertyPathExtractor timestampExtractor = PropertyPathExtractor.from("http://www.w3.org/ns/prov#generatedAtTime");
	private final PropertyPathExtractor versionOfExtractor = PropertyPathExtractor.from("http://purl.org/dc/terms/isVersionOf");
	private LatestStateFilter latestStateFilter;

	@BeforeEach
	void setUp() {
		latestStateFilter = new LatestStateFilter(memberVersionRepository, false, timestampExtractor, versionOfExtractor);
	}

	@Test
	void given_OlderVersionObjectInRepo_when_IsAllowed_then_ReturnTrue() {
		final String versionOf = "https://data.vlaanderen.be/id/perceel/13374D0779-00D003";
		final LocalDateTime newerTimestamp = LocalDateTime.parse("2022-12-29T11:37:27");
		final Model newerModel = createModel();
		final SuppliedMember newerMember = new SuppliedMember("newer-member", newerModel);
		when(memberVersionRepository.isVersionAfterTimestamp(new MemberVersionRecord(versionOf, newerTimestamp))).thenReturn(true);

		final boolean actual = latestStateFilter.isAllowed(newerMember);

		assertThat(actual).isTrue();
	}

	@Test
	void given_NewerVersionObjectInRepo_when_IsAllowed_then_ReturnFalse() {
		final String versionOf = "https://data.vlaanderen.be/id/perceel/13374D0779-00D003";
		final LocalDateTime olderTimestamp = LocalDateTime.parse("2022-12-29T11:37:27");
		final SuppliedMember olderMember = new SuppliedMember("newer-member", createModel());
		when(memberVersionRepository.isVersionAfterTimestamp(new MemberVersionRecord(versionOf, olderTimestamp))).thenReturn(false);

		final boolean actual = latestStateFilter.isAllowed(olderMember);

		assertThat(actual).isFalse();
	}

	@Test
	void test_saveAllowedMember() {
		final String versionOf = "https://data.vlaanderen.be/id/perceel/13374D0779-00D003";
		final LocalDateTime timestamp = LocalDateTime.parse("2022-12-29T11:37:27");
		final SuppliedMember member = new SuppliedMember("newer-member", createModel());

		latestStateFilter.saveAllowedMember(member);

		verify(memberVersionRepository).addMemberVersion(new MemberVersionRecord(versionOf, timestamp));
	}

	@Test
	void given_KeepStateIsFalse_when_DestroyState_then_DestroyStateFromRepo() {
		latestStateFilter.destroyState();

		verify(memberVersionRepository).destroyState();
	}

	@Test
	void given_KeepStateIsTrue_when_DestroyState_then_DoeNotDestroyStateFromRepo() {
		final LatestStateFilter	keepStateFilter = new LatestStateFilter(memberVersionRepository, true, timestampExtractor, versionOfExtractor);

		keepStateFilter.destroyState();

		verifyNoInteractions(memberVersionRepository);
	}

	private Model createModel() {
		final String modelTemplate = """
				@prefix prov:             <http://www.w3.org/ns/prov#> .
				@prefix terms:            <http://purl.org/dc/terms/> .
				
				<https://data.vlaanderen.be/id/perceel/13374D0779-00D003/2022-11-29T11:37:27+01:00>
				        terms:isVersionOf             <https://data.vlaanderen.be/id/perceel/13374D0779-00D003>;
				        prov:generatedAtTime          "2022-12-29T11:37:27+01:00"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
				""";
		return RDFParser.fromString(modelTemplate).lang(Lang.TTL).toModel();
	}

}