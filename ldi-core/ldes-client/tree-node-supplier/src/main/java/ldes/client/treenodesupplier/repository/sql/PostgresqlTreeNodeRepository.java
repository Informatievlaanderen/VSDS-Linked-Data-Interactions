package ldes.client.treenodesupplier.repository.sql;

public class PostgresqlTreeNodeRepository extends SqlTreeNodeRepository {

	private final String instanceName;

	public PostgresqlTreeNodeRepository(String instanceName, EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
		this.instanceName = instanceName;
	}

	@Override
	public void destroyState() {
		entityManagerFactory.destroyState(instanceName);
	}

}
