package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.postgres.PostgresProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite.SqliteProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties.*;

public class HibernateUtil {
	private HibernateUtil() {
	}

	public static EntityManager createEntityManagerFromDatasource(DataSource dataSource, boolean keepState, StatePersistenceStrategy state) {
		var entityManagerFactory = Persistence.createEntityManagerFactory("pu-sql-jpa",
				Map.of(HIBERNATE_DATASOURCE, dataSource,
						HIBERNATE_HBM_2_DDL_AUTO, keepState ? UPDATE : CREATE_DROP,
						HIBERNATE_DIALECT, getHibernateDialect(state)));
		return entityManagerFactory.createEntityManager();
	}

	public static EntityManager createEntityManagerFromProperties(Map<String, String> properties) {
		var entityManagerFactory = Persistence.createEntityManagerFactory("pu-sql-jpa", properties);
		return entityManagerFactory.createEntityManager();
	}

	public static String getHibernateDialect(StatePersistenceStrategy state) {
		return switch (state) {
			case POSTGRES -> PostgresProperties.DIALECT;
			case SQLITE -> SqliteProperties.DIALECT;
			default -> "";
		};
	}
}
