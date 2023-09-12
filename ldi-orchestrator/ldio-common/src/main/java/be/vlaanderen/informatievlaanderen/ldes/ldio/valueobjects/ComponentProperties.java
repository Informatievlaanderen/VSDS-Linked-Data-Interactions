package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ComponentProperties {
	private final Map<String, String> config;

	public ComponentProperties() {
		this(Map.of());
	}

	public ComponentProperties(Map<String, String> inputConfig) {
		this.config = new HashMap<>();
		inputConfig.forEach((key, value) -> this.config.put(removeCasing(key), value));
	}

	public Map<String, String> getConfig() {
		return config;
	}

	public String getProperty(String key) {
		String caseInsensitiveKey = removeCasing(key);
		String value = config.get(caseInsensitiveKey);
		if (value == null) {
			throw new IllegalArgumentException("Missing value for key " + key);
		}
		return value;
	}

	/**
	 * Returns a list when a property is an array. For example
	 *
	 * <pre>
	 *         config:
	 *           url:
	 *             - example.com/api/1
	 *             - example.com/api/2
	 * </pre>
	 *
	 * Will return List.of(example.com/api/1, example.com/api/2)
	 * <br>
	 * <br>
	 * When the property is not an array, for example
	 *
	 * <pre>
	 *         config:
	 *          url: example.com/api/1
	 * </pre>
	 *
	 * Then the property is returned as a singleton list:
	 * List.of(example.com/api/1).
	 * <br>
	 * <br>
	 * Returns empty when the property is not found.
	 *
	 * @param key
	 *            the property key,
	 * @return the found properties
	 */
	public List<String> getPropertyList(String key) {
		final List<String> endpoints = new ArrayList<>();

		getOptionalProperty(key).ifPresent(endpoints::add);

		int i = 0;
		boolean propertyFound;
		do {
			Optional<String> optionalProperty = getOptionalProperty("%s.%d".formatted(key, i++));
			if (optionalProperty.isPresent()) {
				propertyFound = true;
				endpoints.add(optionalProperty.get());
			} else {
				propertyFound = false;
			}
		} while (propertyFound);

		return endpoints;
	}

	public Optional<String> getOptionalProperty(String key) {
		return Optional.ofNullable(config.get(removeCasing(key)));
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

	private String removeCasing(String key) {
		return key
				.toLowerCase()
				.replace("-", "")
				.replace("_", "");
	}
}
