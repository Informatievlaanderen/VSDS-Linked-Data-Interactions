package ldes.client.treenodesupplier.repository.sql;

import be.vlaanderen.informatievlaanderen.ldes.ldi.EntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.MemberRecordEntity;
import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.mapper.MemberRecordEntityMapper;

import java.util.Optional;
import java.util.stream.Stream;

public class SqlMemberRepository implements MemberRepository {
	private final EntityManagerFactory entityManagerFactory;
	private final String instanceName;

	public SqlMemberRepository(String instanceName, EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
		this.instanceName = instanceName;
	}

	@Override
	public Optional<MemberRecord> getTreeMember() {
		return entityManagerFactory.getEntityManager()
				.createNamedQuery("Member.getAllOrderedByCreation", MemberRecordEntity.class)
				.setMaxResults(1)
				.getResultStream()
				.findFirst()
				.map(MemberRecordEntityMapper::toMemberRecord);
	}

	@Override
	public void deleteMember(MemberRecord member) {
		entityManagerFactory.executeStatelessQuery(session -> session
				.createNamedQuery("Member.deleteByMemberId")
				.setParameter("memberId", member.getMemberId())
				.executeUpdate());
	}

	@Override
	public void saveTreeMembers(Stream<MemberRecord> treeMemberStream) {
		entityManagerFactory.getEntityManager().getTransaction().begin();
		treeMemberStream.map(MemberRecordEntityMapper::fromMemberRecord)
				.forEach(entityManagerFactory.getEntityManager()::merge);
		entityManagerFactory.getEntityManager().getTransaction().commit();
	}

	@Override
	public void destroyState() {
		entityManagerFactory.destroyState(instanceName);
	}
}
