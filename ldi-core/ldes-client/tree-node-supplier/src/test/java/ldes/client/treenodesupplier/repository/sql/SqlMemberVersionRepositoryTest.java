package ldes.client.treenodesupplier.repository.sql;

import be.vlaanderen.informatievlaanderen.ldes.ldi.EntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.MemberVersionRecordEntity;
import ldes.client.treenodesupplier.domain.entities.MemberVersionRecord;
import ldes.client.treenodesupplier.repository.mapper.MemberVersionRecordEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SqlMemberVersionRepositoryTest {

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private EntityManager entityManager;

    private final String instanceName = "testInstance";

    private SqlMemberVersionRepository repository;

    @BeforeEach
    void setUp() {
        when(entityManagerFactory.getEntityManager()).thenReturn(entityManager);
        repository = new SqlMemberVersionRepository(entityManagerFactory, instanceName);
    }

    @Test
    void test_addMemberVersion() {
        MemberVersionRecord actual = new MemberVersionRecord("versionOf", LocalDateTime.now());
        MemberVersionRecordEntity entity = MemberVersionRecordEntityMapper.fromMemberVersionRecord(actual);

        repository.addMemberVersion(actual);

        verify(entityManager).merge(argThat((MemberVersionRecordEntity actualEntity) -> actualEntity.getVersionOf().equals(entity.getVersionOf())));
    }

    @Test
    void whenNoOlderVersionPresent_isVersionAfterTimestampIsTrue() {
        TypedQuery<MemberVersionRecordEntity> mockedQuery = mock(TypedQuery.class);
        when(entityManager.createNamedQuery("MemberVersion.findMemberVersionAfterTimestamp", MemberVersionRecordEntity.class)).thenReturn(mockedQuery);
        when(mockedQuery.setParameter(anyString(), any())).thenReturn(mockedQuery);

        MemberVersionRecord memberVersion = new MemberVersionRecord("versionOf", LocalDateTime.now());

        boolean actual = repository.isVersionAfterTimestamp(memberVersion);

        assertThat(actual).isTrue();
    }

    @Test
    void whenOlderVersionIsPresent_isVersionAfterTimestampIsFalse() {
        final String versionOf = "versionOf";
        MemberVersionRecordEntity olderVersion = new MemberVersionRecordEntity(versionOf, LocalDateTime.now().minusDays(1));
        MemberVersionRecord memberVersion = new MemberVersionRecord(versionOf, LocalDateTime.now());

        TypedQuery<MemberVersionRecordEntity> mockedQuery = mock(TypedQuery.class);
        when(entityManager.createNamedQuery("MemberVersion.findMemberVersionAfterTimestamp", MemberVersionRecordEntity.class)).thenReturn(mockedQuery);
        when(mockedQuery.setParameter(anyString(), any())).thenReturn(mockedQuery);
        when(mockedQuery.getResultStream()).thenReturn(Stream.of(olderVersion));

        boolean actual = repository.isVersionAfterTimestamp(memberVersion);

        assertThat(actual).isFalse();
    }

    @Test
    void test_destroyState() {
        repository.destroyState();

        verify(entityManagerFactory).destroyState(instanceName);
    }
}