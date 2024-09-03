package be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite;

import be.vlaanderen.informatievlaanderen.ldes.ldi.AbstractEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;
import org.apache.commons.io.FileUtils;

import javax.persistence.Persistence;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SqliteEntityManagerFactory extends AbstractEntityManagerFactory {
	public static final String PERSISTENCE_UNIT_NAME = "pu-sqlite-jpa";
	private final SqliteProperties properties;
	private static final Map<String, SqliteEntityManagerFactory> instances = new HashMap<>();

	private SqliteEntityManagerFactory(SqliteProperties properties) {
		super(Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties.getProperties()));
		this.properties = properties;
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
	public void destroyState(String instanceName) {
		super.destroyState(instanceName);
		instances.remove(instanceName);
		FileUtils.deleteQuietly(new File(properties.getDatabaseDirectory(), properties.getDatabaseName()));
	}
}
