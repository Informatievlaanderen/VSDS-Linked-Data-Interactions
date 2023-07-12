package ldes.client.treenodesupplier.repository.sqlite;

import ldes.client.treenodesupplier.repository.exception.CreateDirectoryFailedException;
import ldes.client.treenodesupplier.repository.exception.DestroyDbFailedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

@SuppressWarnings("java:S2696")
public class EntityManagerFactory {

	public static final String DATABASE_DIRECTORY = "state";
	public static final String PERSISTENCE_UNIT_NAME = "pu-sqlite-jpa";
	public static final String DATABASE_NAME = "database.db";
	private static EntityManagerFactory instance = null;
	private final EntityManager em;
	private final javax.persistence.EntityManagerFactory emf;
	private boolean databaseDeleted = false;

	private EntityManagerFactory() {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emf.createEntityManager();
	}

	public static synchronized EntityManagerFactory getInstance() {
		if (instance == null) {
			try {
				Files.createDirectory(Paths.get(DATABASE_DIRECTORY));
			} catch (IOException e) {
				throw new CreateDirectoryFailedException(e);
			}
			instance = new EntityManagerFactory();
		}

		return instance;
	}

	public EntityManager getEntityManager() {
		return em;
	}

	public void destroyState() {
		try {
			em.close();
			emf.close();
			instance = null;
			if (!databaseDeleted) {
				Files.delete(Path.of(DATABASE_DIRECTORY, DATABASE_NAME));
				Files.delete(Path.of(DATABASE_DIRECTORY));
				databaseDeleted = true;
			}
		} catch (IOException e) {
			throw new DestroyDbFailedException(e);
		}
	}
}
