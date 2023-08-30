package ldes.client.treenodesupplier.repository.sql;

import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.services.TreeNodeRecordComparator;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;

import javax.persistence.EntityManager;
import java.util.Optional;

public class SqlTreeNodeRepository implements TreeNodeRecordRepository {

	final EntityManagerFactory entityManagerFactory;
	private final EntityManager entityManager;

	public SqlTreeNodeRepository(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
		this.entityManager = entityManagerFactory.getEntityManager();
	}

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
				.min(new TreeNodeRecordComparator());
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

	@Override
	public boolean containsTreeNodeRecords() {
		return entityManager
				.createNamedQuery("TreeNode.count", Long.class)
				.getSingleResult() > 0;
	}

}
