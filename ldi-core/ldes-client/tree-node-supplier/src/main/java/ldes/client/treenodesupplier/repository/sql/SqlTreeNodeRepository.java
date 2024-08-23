package ldes.client.treenodesupplier.repository.sql;

import be.vlaanderen.informatievlaanderen.ldes.ldi.EntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.TreeNodeRecordEntity;
import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.mapper.TreeNodeRecordEntityMapper;

import java.util.Optional;

import javax.persistence.EntityManager;

public class SqlTreeNodeRepository implements TreeNodeRecordRepository {

	final EntityManagerFactory entityManagerFactory;
	private final EntityManager entityManager;
	private final String instanceName;

	public SqlTreeNodeRepository(String instanceName, EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
		this.entityManager = entityManagerFactory.getEntityManager();
		this.instanceName = instanceName;
	}

	@Override
	public void saveTreeNodeRecord(TreeNodeRecord treeNodeRecord) {
		TreeNodeRecordEntity memberRecordEntity = TreeNodeRecordEntityMapper.fromTreeNodeRecord(treeNodeRecord);
		entityManager.getTransaction().begin();
		entityManager.merge(memberRecordEntity);
		entityManager.getTransaction().commit();
	}

	@Override
	public boolean existsById(String treeNodeId) {
		return entityManager
				.createNamedQuery("TreeNode.getById", TreeNodeRecordEntity.class)
				.setParameter("id", treeNodeId)
				.setMaxResults(1)
				.getResultStream()
				.findFirst()
				.isPresent();
	}

	@Override
	public Optional<TreeNodeRecord> getTreeNodeRecordWithStatusAndEarliestNextVisit(TreeNodeStatus treeNodeStatus) {
		return entityManager
				.createNamedQuery("TreeNode.getByStatusAndDate", TreeNodeRecordEntity.class)
				.setParameter("treeNodeStatus", treeNodeStatus.name())
				.setMaxResults(1)
				.getResultStream()
				.findFirst()
				.map(TreeNodeRecordEntityMapper::toTreeNode);
	}

	@Override
	public boolean existsByIdAndStatus(String treeNodeId, TreeNodeStatus treeNodeStatus) {
		return entityManager
				.createNamedQuery("TreeNode.getByIdAndStatus", TreeNodeRecordEntity.class)
				.setParameter("id", treeNodeId)
				.setParameter("treeNodeStatus", treeNodeStatus.name())
				.setMaxResults(1)
				.getResultStream()
				.findFirst()
				.isPresent();
	}

	@Override
	public void destroyState() {
		entityManagerFactory.destroyState(instanceName);
	}

	@Override
	public boolean containsTreeNodeRecords() {
		return entityManager
				.createNamedQuery("TreeNode.getAll", TreeNodeRecordEntity.class)
				.setMaxResults(1)
				.getResultStream()
				.findFirst()
				.isPresent();
	}

	@Override
	public void resetContext() {
		entityManager.clear();
	}

}
