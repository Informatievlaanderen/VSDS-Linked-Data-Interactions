package be.vlaanderen.informatievlaanderen.ldes.ldi.repository.sqlite;

import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.EntityManagerFactory;
import org.apache.commons.io.FileUtils;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("java:S2696")
public class SqliteEntityManagerFactory implements EntityManagerFactory {

	public static final String DATABASE_DIRECTORY = "ldes-client";
	public static final String PERSISTENCE_UNIT_NAME_CLIENT = "pu-sqlite-jpa-client";
	public static final String PERSISTENCE_UNIT_SQLITE_CHANGE_DETECTION_FILTER = "pu-sqlite-jpa-change-detection-filter";
	public static final String DATABASE_DIRECTORY_KEY = "directory";
	public static final String HIBERNATE_HBM_2_DDL_AUTO = "hibernate.hbm2ddl.auto";
	public static final String UPDATE = "update";
	public static final String CREATE_DROP = "create-drop";
	private final String databaseDirectory;
	private final String databaseName;
	private static final Map<String, SqliteEntityManagerFactory> instances = new HashMap<>();
	private final EntityManager em;
	private final javax.persistence.EntityManagerFactory emf;

	private SqliteEntityManagerFactory(String persistenceUnitName, String databaseDirectory, String instanceName, String hibernateDdlAuto) {
		this.databaseDirectory = databaseDirectory;
		this.databaseName = instanceName + ".db";
		emf = Persistence.createEntityManagerFactory(persistenceUnitName, Map.of(
				"javax.persistence.jdbc.url", "jdbc:sqlite:./%s/%s".formatted(databaseDirectory, databaseName),
				HIBERNATE_HBM_2_DDL_AUTO, hibernateDdlAuto
		));
		em = emf.createEntityManager();
	}

	public static synchronized SqliteEntityManagerFactory getClientInstance(String instanceName, Map<String, String> properties) {
		return getInstance(PERSISTENCE_UNIT_NAME_CLIENT, instanceName, properties);
	}

	public static synchronized SqliteEntityManagerFactory getInstance(String persistenceUnitName, String instanceName, Map<String, String> properties) {
		final String databaseDirectory = properties.getOrDefault(DATABASE_DIRECTORY_KEY, DATABASE_DIRECTORY);
		return instances.computeIfAbsent(instanceName, s -> {
			try {
				Files.createDirectories(Paths.get(databaseDirectory));
			} catch (IOException e) {
				throw new CreateDirectoryFailedException(e);
			}
			return new SqliteEntityManagerFactory(persistenceUnitName, databaseDirectory, instanceName, properties.getOrDefault(HIBERNATE_HBM_2_DDL_AUTO, UPDATE));
		});
	}


	public EntityManager getEntityManager() {
		return em;
	}

	public void destroyState(String instanceName) {
		em.close();
		emf.close();
		instances.remove(instanceName);
		FileUtils.deleteQuietly(new File(databaseDirectory, databaseName));
	}
}
