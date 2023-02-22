package be.vlaanderen.informatievlaanderen.ldes.client.member.sqlite;

import be.vlaanderen.informatievlaanderen.ldes.client.member.MemberRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class SqliteMemberRepository implements MemberRepository {

	private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("pu-sqlite-jpa");

	private final EntityManager entityManager = entityManagerFactory.createEntityManager();

	@Override
	public boolean isProcessedMember(String memberId) {
		return ((Number) entityManager
				.createNamedQuery("Member.countById")
				.setParameter("id", memberId)
				.getSingleResult()).longValue() > 0;
	}

	@Override
	public void removeProcessedMembers() {
		entityManager.getTransaction().begin();
		entityManager.createNamedQuery("Member.deleteAll").executeUpdate();
		entityManager.getTransaction().commit();
	}

	@Deprecated(since = "Issue with db still being used")
	private void destroyDb() {
		try {
			entityManager.close();
			entityManagerFactory.close();
			Files.delete(Path.of("member-database.db"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long countProcessedMembers() {
		return ((Number) entityManager.createNamedQuery("Member.countAll").getSingleResult()).longValue();
	}

	@Override
	public void addProcessedMember(String memberId) {
		if (!isProcessedMember(memberId)) {
			Member member = new Member(memberId);
			entityManager.getTransaction().begin();
			entityManager.persist(member);
			entityManager.getTransaction().commit();
		}
	}

	@Override
	public void clearState() {
		removeProcessedMembers();
    //destroyDb();
	}
}
