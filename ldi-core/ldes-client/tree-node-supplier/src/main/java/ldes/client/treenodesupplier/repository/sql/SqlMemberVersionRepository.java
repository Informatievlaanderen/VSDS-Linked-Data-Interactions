package ldes.client.treenodesupplier.repository.sql;

import be.vlaanderen.informatievlaanderen.ldes.ldi.EntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.MemberVersionRecordEntity;
import jakarta.persistence.EntityManager;
import ldes.client.treenodesupplier.domain.entities.MemberVersionRecord;
import ldes.client.treenodesupplier.repository.MemberVersionRepository;
import ldes.client.treenodesupplier.repository.mapper.MemberVersionRecordEntityMapper;

public class SqlMemberVersionRepository implements MemberVersionRepository {

    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    private final String instanceName;

    public SqlMemberVersionRepository(String instanceName, EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.instanceName = instanceName;
        this.entityManager = entityManagerFactory.getEntityManager();
    }

    @Override
    public void addMemberVersion(MemberVersionRecord memberVersion) {
        var tx = entityManager.getTransaction();
        tx.begin();
        entityManager.merge(MemberVersionRecordEntityMapper.fromMemberVersionRecord(memberVersion));
        tx.commit();
    }

    @Override
    public boolean isVersionAfterTimestamp(MemberVersionRecord memberVersion) {
        var tx = entityManager.getTransaction();
        tx.begin();
        var isVersionAfterTimestamp = entityManager
                .createNamedQuery("MemberVersion.findMemberVersionAfterTimestamp", MemberVersionRecordEntity.class)
                .setParameter("versionOf", memberVersion.getVersionOf())
                .setParameter("timestamp", memberVersion.getTimestamp())
                .getResultStream()
                .findFirst()
                .isEmpty();
        tx.commit();
        return isVersionAfterTimestamp;
    }

    @Override
    public void destroyState() {
        entityManagerFactory.destroyState(instanceName);
    }
}
