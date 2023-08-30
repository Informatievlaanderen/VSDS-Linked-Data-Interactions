package ldes.client.treenodesupplier.repository.filebased;

import ldes.client.treenodesupplier.repository.filebased.exception.CreateDirectoryFailedException;
import ldes.client.treenodesupplier.repository.filebased.exception.DestroyStateFailedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class FileManagerFactory {
	public static final String STATE_FOLDER = "ldes-client";
	private static final Map<String, FileManagerFactory> instances = new HashMap<>();
	private static boolean stateDeleted = false;

	public static synchronized FileManagerFactory getInstance(String instanceName) {
		return instances.computeIfAbsent(instanceName, s -> {
			try {
				Files.createDirectories(Paths.get("%s/%s".formatted(STATE_FOLDER, instanceName)));
			} catch (IOException e) {
				throw new CreateDirectoryFailedException(e);
			}
			var instance = new FileManagerFactory();
			stateDeleted = false;
			return instance;
		});
	}

	public FileManager getFileManager(String directory) {
		return new FileManager(directory);
	}

	public void destroyState(String instanceName) {
		try {
			instances.remove("%s/%s".formatted(STATE_FOLDER, instanceName));
			if (!stateDeleted) {
				deleteState("%s/%s".formatted(STATE_FOLDER, instanceName));
			}
		} catch (IOException e) {
			throw new DestroyStateFailedException(e);
		}
	}

	private void deleteState(String instanceName) throws IOException {
		Path statePath = Path.of(instanceName);
		try (Stream<Path> list = Files.list(statePath)) {
			list.forEach(path -> {
				try {
					Files.delete(path);
				} catch (IOException e) {
					throw new DestroyStateFailedException(e);
				}
			});
		}
		Files.delete(statePath);
		stateDeleted = true;
	}
}
