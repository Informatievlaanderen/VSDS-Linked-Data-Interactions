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
	public static final String PERSISTENCE_UNIT_NAME = "pu-sqlite-jpa";
	public static final String DATABASE_DIRECTORY_KEY = "directory";
	private final String databaseDirectory;
	private final String databaseName;
	private static final Map<String, SqliteEntityManagerFactory> instances = new HashMap<>();
	private final EntityManager em;
	private final javax.persistence.EntityManagerFactory emf;

	private SqliteEntityManagerFactory(String databaseDirectory, String instanceName) {
		this.databaseDirectory = databaseDirectory;
		this.databaseName = instanceName + ".db";
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, Map.of("javax.persistence.jdbc.url",
				"jdbc:sqlite:./%s/%s".formatted(databaseDirectory, databaseName)));
		em = emf.createEntityManager();
	}

	public static synchronized SqliteEntityManagerFactory getInstance(String instanceName, Map<String, String> properties) {
		final String databaseDirectory = properties.getOrDefault(DATABASE_DIRECTORY_KEY, DATABASE_DIRECTORY);
		return instances.computeIfAbsent(instanceName, s -> {
			try {
				Files.createDirectories(Paths.get(databaseDirectory));
			} catch (IOException e) {
				throw new CreateDirectoryFailedException(e);
			}
			return new SqliteEntityManagerFactory(databaseDirectory, instanceName);
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
