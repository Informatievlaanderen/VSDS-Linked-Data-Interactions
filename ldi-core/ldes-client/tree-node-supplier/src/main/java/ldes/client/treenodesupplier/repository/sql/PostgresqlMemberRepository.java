package ldes.client.treenodesupplier.repository.sql;

public class PostgresqlMemberRepository extends SqlMemberRepository {

	private final String instanceName;

	public PostgresqlMemberRepository(String instanceName, EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
		this.instanceName = instanceName;
	}

	@Override
	public void destroyState() {
		entityManagerFactory.destroyState(instanceName);
	}

}
