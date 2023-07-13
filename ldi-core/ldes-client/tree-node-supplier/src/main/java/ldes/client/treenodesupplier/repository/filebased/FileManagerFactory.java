package ldes.client.treenodesupplier.repository.filebased;

import ldes.client.treenodesupplier.repository.filebased.exception.CreateDirectoryFailedException;
import ldes.client.treenodesupplier.repository.filebased.exception.DestroyStateFailedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileManagerFactory {
	private static FileManagerFactory instance = null;
	private final FileManager fileManager;
	public static final String STATE_DIRECTORY = "state";
	private static boolean stateDeleted = false;

	public FileManagerFactory() {
		fileManager = new FileManager();
	}

	public static synchronized FileManagerFactory getInstance() {
		if (instance == null) {
			try {
				Files.createDirectories(Paths.get(STATE_DIRECTORY));
			} catch (IOException e) {
				throw new CreateDirectoryFailedException(e);
			}
			instance = new FileManagerFactory();
			stateDeleted = false;
		}

		return instance;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public static void destroyState() {
		try {
			instance = null;
			if (!stateDeleted) {
				deleteState();
			}
		} catch (IOException e) {
			throw new DestroyStateFailedException(e);
		}
	}

	private static void deleteState() throws IOException {
		try (Stream<Path> list = Files.list(Paths.get(STATE_DIRECTORY))) {
			list.forEach(path -> {
				try {
					Files.delete(path);
				} catch (IOException e) {
					throw new DestroyStateFailedException(e);
				}
			});
		}
		Files.delete(Paths.get(STATE_DIRECTORY));
		stateDeleted = true;
	}
}
