package ldes.client.treenodesupplier.repository.sql;

import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.repository.MemberRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

public class SqlMemberRepository implements MemberRepository {
	final EntityManagerFactory entityManagerFactory;
	private final EntityManager entityManager;
	private final String instanceName;

	public SqlMemberRepository(String instanceName, EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
		this.entityManager = entityManagerFactory.getEntityManager();
		this.instanceName = instanceName;
	}

	@Override
	public Optional<MemberRecord> getNextUnprocessedMember() {
		return entityManager
				.createQuery("select m from MemberRecordEntity m where m.processed = false order by m.createdAt",
						MemberRecordEntity.class)
				.getResultStream()
				.map(MemberRecordEntity::toMemberRecord)
				.findFirst();
	}

	@Override
	public void markAsProcessed(MemberRecord member) {
		entityManager.getTransaction().begin();
		entityManager
				.createQuery("update MemberRecordEntity set processed = true where id = :id")
				.setParameter("id", member.getMemberId())
				.executeUpdate();
		entityManager.getTransaction().commit();
	}

	@Override
	public void insertTreeMembers(Set<MemberRecord> treeMembers) {
		entityManager.getTransaction().begin();
		treeMembers.stream().map(MemberRecordEntity::fromMemberRecord).forEach(entityManager::persist);
		entityManager.getTransaction().commit();
	}

	@Override
	public void destroyState() {
		entityManagerFactory.destroyState(instanceName);
	}

	// TODO TVB: wat met unprocessed
	// TODO TVB: we verwerken altijd eerst members voor we nieuwe treenode request doen
	// TODO TVB: tussenstatus? immutable not deleted -> immutable deleted
	@Override
	public void deleteProcessedMembersByTreeNode(String treeNodeUrl) {
		entityManager.getTransaction().begin();
		entityManager
				.createQuery("delete MemberRecordEntity where treeNodeUrl = :url and processed = true")
				.setParameter("url", treeNodeUrl)
				.executeUpdate();
		entityManager.getTransaction().commit();
	}

	@Override
	public Set<String> findMemberIdsByTreeNode(String treeNodeUrl) {
		return entityManager
				.createQuery("select m.id from MemberRecordEntity m where m.treeNodeUrl = :url", String.class)
				.setParameter("url", treeNodeUrl)
				.getResultStream()
				.collect(Collectors.toSet());
	}
}
