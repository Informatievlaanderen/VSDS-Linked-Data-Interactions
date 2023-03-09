package ldes.client.treenodesupplier.repository.sqlite;

import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;

import javax.persistence.EntityManager;
import java.util.Optional;

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
		return ((Number) entityManager
				.createNamedQuery("TreeNode.countById")
				.setParameter("id", treeNodeId)
				.getSingleResult()).longValue() > 0;
	}

	@Override
	public Optional<TreeNodeRecord> getOneTreeNodeRecordWithStatus(TreeNodeStatus treeNodeStatus) {
		return entityManager
				.createNamedQuery("TreeNode.getByTreeNodeStatus")
				.setParameter("treeNodeStatus", treeNodeStatus)
				.getResultStream()
				.map(x -> ((TreeNodeRecordEntity) x).toTreenode())
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
