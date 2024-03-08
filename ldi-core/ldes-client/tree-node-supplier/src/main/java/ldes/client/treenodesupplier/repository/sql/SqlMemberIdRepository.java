package ldes.client.treenodesupplier.repository.sql;

import ldes.client.treenodesupplier.repository.MemberIdRepository;

import javax.persistence.EntityManager;

public class SqlMemberIdRepository implements MemberIdRepository {
    final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    private final String instanceName;

    public SqlMemberIdRepository(String instanceName, EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.entityManager = entityManagerFactory.getEntityManager();
        this.instanceName = instanceName;
    }

    @Override
    public void addMemberId(String memberId) {
        MemberIdRecordEntity memberRecordEntity = MemberIdRecordEntity.fromId(memberId);
        entityManager.getTransaction().begin();
        entityManager.merge(memberRecordEntity);
        entityManager.getTransaction().commit();
    }

    @Override
    public boolean contains(String memberId) {
        return entityManager.createNamedQuery("MemberId.get", MemberIdRecordEntity.class)
                .setParameter("id", memberId)
                .getResultStream()
                .findAny().isPresent();
    }
    @Override
    public void destroyState() {
        entityManagerFactory.destroyState(instanceName);
    }
}
