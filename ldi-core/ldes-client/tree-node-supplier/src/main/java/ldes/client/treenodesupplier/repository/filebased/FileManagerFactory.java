package ldes.client.treenodesupplier.repository.filebased;

import ldes.client.treenodesupplier.repository.filebased.exception.CreateDirectoryFailedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileManagerFactory {
	private FileManagerFactory() {
	}

	public static final String STATE_FOLDER = "ldes-client";
	private static final Map<String, FileManager> instances = new HashMap<>();

	public static synchronized FileManager getInstance(String instanceName) {
		return instances.computeIfAbsent(instanceName, s -> {
			try {
				Files.createDirectories(Paths.get("%s/%s".formatted(STATE_FOLDER, instanceName)));
			} catch (IOException e) {
				throw new CreateDirectoryFailedException(e);
			}
			return new FileManager(instanceName);
		});
	}

	public static void removeInstance(String instanceName) {
		instances.remove(instanceName);
	}
}
