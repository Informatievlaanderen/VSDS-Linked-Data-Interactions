package ldes.client.treenodesupplier.repository.sql.sqlite;

import ldes.client.treenodesupplier.repository.filebased.exception.CreateDirectoryFailedException;
import ldes.client.treenodesupplier.repository.sql.EntityManagerFactory;
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
	public final String databaseName;
	private static final Map<String, SqliteEntityManagerFactory> instances = new HashMap<>();
	private final EntityManager em;
	private final javax.persistence.EntityManagerFactory emf;

	private SqliteEntityManagerFactory(String instanceName) {
		this.databaseName = instanceName + ".db";
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, Map.of("javax.persistence.jdbc.url",
				"jdbc:sqlite:./%s/%s".formatted(DATABASE_DIRECTORY, databaseName)));
		em = emf.createEntityManager();
	}

	public static synchronized SqliteEntityManagerFactory getInstance(String instanceName) {
		return instances.computeIfAbsent(instanceName, s -> {
			try {
				Files.createDirectories(Paths.get(DATABASE_DIRECTORY));
			} catch (IOException e) {
				throw new CreateDirectoryFailedException(e);
			}
			return new SqliteEntityManagerFactory(instanceName);
		});
	}

	public EntityManager getEntityManager() {
		return em;
	}

	public void destroyState(String instanceName) {
		em.close();
		emf.close();
		instances.remove(instanceName);
		FileUtils.deleteQuietly(new File(DATABASE_DIRECTORY, databaseName));
	}
}
