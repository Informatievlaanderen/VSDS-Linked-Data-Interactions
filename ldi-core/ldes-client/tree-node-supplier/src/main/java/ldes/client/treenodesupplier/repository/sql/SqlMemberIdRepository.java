package ldes.client.treenodesupplier.repository.sql;

import ldes.client.treenodesupplier.domain.valueobject.MemberStatus;
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
        entityManager
                .createNamedQuery("Member.addId", String.class)
                .setParameter("id", memberId)
                .executeUpdate();
    }

    @Override
    public boolean contains(String memberId) {
        return entityManager
                .createNamedQuery("Member.get", String.class)
                .setParameter("Id", memberId)
                .getResultStream()
                .findAny().isPresent();
    }
}
