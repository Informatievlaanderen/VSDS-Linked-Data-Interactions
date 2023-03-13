package ldes.client.treenodesupplier.repository.sqlite;

import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;

import java.util.Optional;

import javax.persistence.EntityManager;

public class SqliteTreeNodeRepository implements TreeNodeRecordRepository {

	private final EntityManagerFactory entityManagerFactory = EntityManagerFactory.getInstance();
	private final EntityManager entityManager = entityManagerFactory.getEntityManager();

	@Override
	public void saveTreeNodeRecord(TreeNodeRecord treeNodeRecord) {
		TreeNodeRecordEntity memberRecordEntity = TreeNodeRecordEntity.fromTreeNodeRecord(treeNodeRecord);
		entityManager.getTransaction().begin();
		entityManager.merge(memberRecordEntity);
		entityManager.getTransaction().commit();
	}

	@Override
	public boolean existsById(String treeNodeId) {
		return entityManager
				.createNamedQuery("TreeNode.countById", Long.class)
				.setParameter("id", treeNodeId)
				.getSingleResult() > 0;
	}

	@Override
	public Optional<TreeNodeRecord> getOneTreeNodeRecordWithStatus(TreeNodeStatus treeNodeStatus) {
		return entityManager
				.createNamedQuery("TreeNode.getByTreeNodeStatus", TreeNodeRecordEntity.class)
				.setParameter("treeNodeStatus", treeNodeStatus)
				.getResultStream()
				.map(TreeNodeRecordEntity::toTreeNode)
				.findFirst();
	}

	@Override
	public boolean existsByIdAndStatus(String treeNodeId, TreeNodeStatus treeNodeStatus) {
		return ((Number) entityManager
				.createNamedQuery("TreeNode.countByIdAndStatus")
				.setParameter("id", treeNodeId)
				.setParameter("treeNodeStatus", treeNodeStatus)
				.getSingleResult()).longValue() > 0;
	}

	@Override
	public void destroyState() {
		entityManagerFactory.destroyState();
	}
}
