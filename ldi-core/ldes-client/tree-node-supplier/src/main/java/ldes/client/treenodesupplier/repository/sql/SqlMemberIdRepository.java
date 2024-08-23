package ldes.client.treenodesupplier.repository.sql;

import be.vlaanderen.informatievlaanderen.ldes.ldi.EntityManagerFactory;
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

//    @Override
//    public void addMemberId(String memberId) {
//        org.hibernate.Session session = entityManager.unwrap(org.hibernate.Session.class);
//        session.doWork(c -> {
//            var statelessSession = session.getSessionFactory().openStatelessSession(c);
//            try (statelessSession) {
//                statelessSession
//                    .createNativeQuery("INSERT INTO MemberIdRecordEntity(id) VALUES (:memberId)")
//                    .setParameter("memberId", memberId)
//                    .executeUpdate();
//            }
//        });
//    }

//    @Override
//    public boolean contains(String memberId) {
//        return entityManager.createNamedQuery("MemberId.get", MemberIdRecordEntity.class)
//                .setParameter("id", memberId)
//                .getResultStream()
//                .findAny().isPresent();
//    }

    @Override
    public boolean addMemberIdIfNotExists(String memberId) {
        org.hibernate.Session session = entityManager.unwrap(org.hibernate.Session.class);
        var affectedCount = session.doReturningWork(c -> {
            var statelessSession = session.getSessionFactory().openStatelessSession(c);
            try (statelessSession) {
                return statelessSession
                        .createNativeQuery("INSERT INTO MemberIdRecordEntity(id) VALUES (:memberId) ON CONFLICT DO NOTHING")
                        .setParameter("memberId", memberId)
                        .executeUpdate();
            }
        });
        return affectedCount > 0;
    }

    @Override
    public void destroyState() {
        entityManagerFactory.destroyState(instanceName);
    }
}
