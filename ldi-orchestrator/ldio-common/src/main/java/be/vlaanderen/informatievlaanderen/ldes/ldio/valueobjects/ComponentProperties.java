package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public class ComponentProperties {
	private final Map<String, String> config;

	public ComponentProperties() {
		this(Map.of());
	}

	public ComponentProperties(Map<String, String> config) {
		this.config = config;
	}

	public Map<String, String> getConfig() {
		return config;
	}

	public String getProperty(String key) {
		String value = config.get(key);
		if (value == null) {
			throw new IllegalArgumentException("Missing value for key " + key);
		}
		return value;
	}

	public Optional<String> getOptionalProperty(String key) {
		return Optional.ofNullable(config.get(key));
	}

	public Optional<Boolean> getOptionalBoolean(String key) {
		return getOptionalProperty(key).map(Boolean::parseBoolean);
	}

	public Optional<Integer> getOptionalInteger(String key) {
		return getOptionalProperty(key).map(Integer::valueOf);
	}

	/**
	 * Returns the configuration value from a file if it exists or an empty Optional
	 * otherwise
	 *
	 * @param key
	 *            the name of a file that contains the configuration value
	 * @return the configuration value from the file if it exists and is readable,
	 *         an empty Optional otherwise.
	 * @throws IllegalArgumentException
	 *             if the file doesn't exist or isn't readable.
	 */
	public Optional<String> getOptionalPropertyFromFile(String key) {
		String file = getProperty(key);
		Path path = Path.of(file);

		if (!Files.exists(path)) {
			return Optional.empty();
		}

		try {
			if (!Files.isReadable(path)) {
				throw new IllegalArgumentException("File doesn't exist or isn't readable: " + file);
			}

			return Optional.ofNullable(Files.readString(path));
		} catch (IOException ioe) {
			throw new IllegalArgumentException("Unreadable file: " + file);
		}
	}
}
