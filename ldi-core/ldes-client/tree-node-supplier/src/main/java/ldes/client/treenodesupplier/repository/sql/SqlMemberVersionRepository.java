package ldes.client.treenodesupplier.repository.sql;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.MemberVersionRecordEntity;
import ldes.client.treenodesupplier.domain.entities.MemberVersionRecord;
import ldes.client.treenodesupplier.repository.MemberVersionRepository;
import ldes.client.treenodesupplier.repository.mapper.MemberVersionRecordEntityMapper;

import javax.persistence.EntityManager;

public class SqlMemberVersionRepository implements MemberVersionRepository {

	private final EntityManager entityManager;

	public SqlMemberVersionRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public void addMemberVersion(MemberVersionRecord memberVersion) {
		entityManager.merge(MemberVersionRecordEntityMapper.fromMemberVersionRecord(memberVersion));
	}

	@Override
	public boolean isVersionAfterTimestamp(MemberVersionRecord memberVersion) {
		return entityManager
				.createNamedQuery("MemberVersion.findMemberVersionAfterTimestamp", MemberVersionRecordEntity.class)
				.setParameter("versionOf", memberVersion.getVersionOf())
				.setParameter("timestamp", memberVersion.getTimestamp())
				.getResultStream()
				.findFirst()
				.isEmpty();
	}

	@Override
	public void destroyState() {
		if (entityManager.isOpen()) {
			entityManager.close();
		}
	}
}
