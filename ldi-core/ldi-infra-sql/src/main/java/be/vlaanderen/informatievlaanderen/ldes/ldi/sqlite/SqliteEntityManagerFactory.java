package be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite;

import be.vlaanderen.informatievlaanderen.ldes.ldi.EntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;
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

	public static final String PERSISTENCE_UNIT_NAME = "pu-sqlite-jpa";
	private final SqliteProperties properties;
	private static final Map<String, SqliteEntityManagerFactory> instances = new HashMap<>();
	private final EntityManager em;
	private final javax.persistence.EntityManagerFactory emf;

	private SqliteEntityManagerFactory(SqliteProperties properties) {
		this.properties = properties;
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties.getProperties());
		em = emf.createEntityManager();
	}

	public static synchronized SqliteEntityManagerFactory getInstance(HibernateProperties hibernateProperties) {
		if (!(hibernateProperties instanceof SqliteProperties properties)) {
			throw new IllegalArgumentException("Invalid properties for SqliteEntityManagerFactory provided");
		}
		return instances.computeIfAbsent(properties.getInstanceName(), s -> {
			try {
				Files.createDirectories(Paths.get(properties.getDatabaseDirectory()));
			} catch (IOException e) {
				throw new CreateDirectoryFailedException(e);
			}
			return new SqliteEntityManagerFactory(properties);
		});
	}

	@Override
	public EntityManager getEntityManager() {
		return em;
	}

	@Override
	public void destroyState(String instanceName) {
		em.close();
		emf.close();
		instances.remove(instanceName);
		FileUtils.deleteQuietly(new File(properties.getDatabaseDirectory(), properties.getDatabaseName()));
	}
}
