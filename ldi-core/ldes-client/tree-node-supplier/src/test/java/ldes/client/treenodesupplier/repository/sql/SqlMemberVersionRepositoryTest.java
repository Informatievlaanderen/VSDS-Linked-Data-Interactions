package ldes.client.treenodesupplier.repository.sql;

import be.vlaanderen.informatievlaanderen.ldes.ldi.EntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.MemberVersionRecordEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import ldes.client.treenodesupplier.domain.entities.MemberVersionRecord;
import ldes.client.treenodesupplier.repository.mapper.MemberVersionRecordEntityMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    void test_addMemberVersion() {
        when(entityManager.getTransaction()).thenReturn(mock());
        when(entityManagerFactory.getEntityManager()).thenReturn(entityManager);
        repository = new SqlMemberVersionRepository(instanceName, entityManagerFactory);

        MemberVersionRecord actual = new MemberVersionRecord("versionOf", LocalDateTime.now());
        MemberVersionRecordEntity entity = MemberVersionRecordEntityMapper.fromMemberVersionRecord(actual);

        repository.addMemberVersion(actual);

        verify(entityManager).merge(argThat((MemberVersionRecordEntity actualEntity) -> actualEntity.getVersionOf().equals(entity.getVersionOf())));
    }

    @Test
    void whenNoOlderVersionPresent_isVersionAfterTimestampIsTrue() {
        when(entityManager.getTransaction()).thenReturn(mock());
        when(entityManagerFactory.getEntityManager()).thenReturn(entityManager);
        repository = new SqlMemberVersionRepository(instanceName, entityManagerFactory);

        TypedQuery<MemberVersionRecordEntity> mockedQuery = mock();
        when(entityManager.createNamedQuery("MemberVersion.findMemberVersionAfterTimestamp", MemberVersionRecordEntity.class)).thenReturn(mockedQuery);
        when(mockedQuery.setParameter(anyString(), any())).thenReturn(mockedQuery);

        MemberVersionRecord memberVersion = new MemberVersionRecord("versionOf", LocalDateTime.now());

        boolean actual = repository.isVersionAfterTimestamp(memberVersion);

        assertThat(actual).isTrue();
    }

    @Test
    void whenOlderVersionIsPresent_isVersionAfterTimestampIsFalse() {
        when(entityManager.getTransaction()).thenReturn(mock());
        when(entityManagerFactory.getEntityManager()).thenReturn(entityManager);
        repository = new SqlMemberVersionRepository(instanceName, entityManagerFactory);

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
        when(entityManagerFactory.getEntityManager()).thenReturn(entityManager);
        repository = new SqlMemberVersionRepository(instanceName, entityManagerFactory);

        repository.destroyState();

        verify(entityManagerFactory).destroyState(instanceName);
    }
}