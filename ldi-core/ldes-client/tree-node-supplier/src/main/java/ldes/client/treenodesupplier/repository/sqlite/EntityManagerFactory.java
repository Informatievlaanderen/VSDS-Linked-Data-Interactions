package ldes.client.treenodesupplier.repository.sqlite;

import ldes.client.treenodesupplier.repository.exception.DestroyDbFailedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

@SuppressWarnings("java:S2696")
public class EntityManagerFactory {

	private static EntityManagerFactory instance = null;
	private final EntityManager em;
	private final javax.persistence.EntityManagerFactory emf;
	private boolean databaseDeleted = false;

	private EntityManagerFactory() {
		emf = Persistence.createEntityManagerFactory("pu-sqlite-jpa");
		em = emf.createEntityManager();
	}

	public static synchronized EntityManagerFactory getInstance() {
		if (instance == null) {
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
				Files.delete(Path.of("database.db"));
				databaseDeleted = true;
			}
		} catch (IOException e) {
			throw new DestroyDbFailedException(e);
		}
	}
}
