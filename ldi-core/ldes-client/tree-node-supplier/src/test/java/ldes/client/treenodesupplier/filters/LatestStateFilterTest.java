package ldes.client.treenodesupplier.filters;

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

	private static final String VERSION_OF = "https://data.vlaanderen.be/id/perceel/13374D0779-00D003";

	@Mock
	private MemberVersionRepository memberVersionRepository;
	private final String timestampPath = "http://www.w3.org/ns/prov#generatedAtTime";
	private final String versionOfPath = "http://purl.org/dc/terms/isVersionOf";
	private final String memberId = "https://data.vlaanderen.be/id/perceel/13374D0779-00D003/2022-11-29T11:37:27+01:00";
	private LatestStateFilter latestStateFilter;

	@BeforeEach
	void setUp() {
		latestStateFilter = new LatestStateFilter(memberVersionRepository, false, timestampPath, versionOfPath);
	}

	@Test
	void given_OlderVersionObjectInRepo_when_IsAllowed_then_ReturnTrue() {
		final LocalDateTime newerTimestamp = LocalDateTime.parse("2022-12-29T11:37:27");
		final Model newerModel = createModel();
		final SuppliedMember newerMember = new SuppliedMember(memberId, newerModel);
		when(memberVersionRepository.isVersionAfterTimestamp(new MemberVersionRecord(VERSION_OF, newerTimestamp))).thenReturn(true);

		final boolean actual = latestStateFilter.saveMemberIfAllowed(newerMember);

		assertThat(actual).isTrue();
		verify(memberVersionRepository).addMemberVersion(new MemberVersionRecord(VERSION_OF, any()));
	}

	@Test
	void given_NewerVersionObjectInRepo_when_IsAllowed_then_ReturnFalse() {
		final LocalDateTime olderTimestamp = LocalDateTime.parse("2022-12-29T11:37:27");
		final SuppliedMember olderMember = new SuppliedMember(memberId, createModel());
		when(memberVersionRepository.isVersionAfterTimestamp(new MemberVersionRecord(VERSION_OF, olderTimestamp))).thenReturn(false);

		final boolean actual = latestStateFilter.saveMemberIfAllowed(olderMember);

		assertThat(actual).isFalse();
		verify(memberVersionRepository).addMemberVersion(new MemberVersionRecord(VERSION_OF, any()));
	}

//	@Test
//	void test_saveAllowedMember() {
//		final SuppliedMember member = new SuppliedMember(memberId, createModel());
//
//		latestStateFilter.saveAllowedMember(member);
//
//
//		verify(memberVersionRepository).addMemberVersion(new MemberVersionRecord(VERSION_OF, any()));
//	}

	@Test
	void given_KeepStateIsFalse_when_DestroyState_then_DestroyStateFromRepo() {
		latestStateFilter.destroyState();

		verify(memberVersionRepository).destroyState();
	}

	@Test
	void given_KeepStateIsTrue_when_DestroyState_then_DoeNotDestroyStateFromRepo() {
		final LatestStateFilter	keepStateFilter = new LatestStateFilter(memberVersionRepository, true, timestampPath, versionOfPath);

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