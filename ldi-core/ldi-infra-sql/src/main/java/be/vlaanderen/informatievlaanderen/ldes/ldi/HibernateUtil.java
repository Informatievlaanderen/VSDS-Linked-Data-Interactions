package be.vlaanderen.informatievlaanderen.ldes.ldi;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.util.Map;

public class HibernateUtil {
	static final String HIBERNATE_DATASOURCE = "hibernate.connection.datasource";
	static final String HIBERNATE_HBM_2_DDL_AUTO = "hibernate.hbm2ddl.auto";
	static final String UPDATE = "update";
	static final String CREATE_DROP = "create-drop";

	public static EntityManager createEntityManagerFromDatasource(DataSource dataSource, boolean keepState) {
		var entityManagerFactory = Persistence.createEntityManagerFactory("pu-sql-jpa",
				Map.of(HIBERNATE_DATASOURCE, dataSource,
						HIBERNATE_HBM_2_DDL_AUTO, keepState ? UPDATE : CREATE_DROP));
		return entityManagerFactory.createEntityManager();
	}

	public static EntityManager createEntityManagerFromProperties(Map<String, String> properties) {
		var entityManagerFactory = Persistence.createEntityManagerFactory("pu-sql-jpa", properties);
		return entityManagerFactory.createEntityManager();
	}
}
