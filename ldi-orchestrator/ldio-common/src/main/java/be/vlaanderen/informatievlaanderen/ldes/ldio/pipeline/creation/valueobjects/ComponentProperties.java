package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.ConfigPropertyMissingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ComponentProperties {
	private String pipelineName;
	private String componentName;
	private final Map<String, String> config;

	public ComponentProperties(String pipelineName, String componentName) {
		this(pipelineName, componentName, Map.of());
	}

	public ComponentProperties(String pipelineName, String componentName, Map<String, String> inputConfig) {
		this.pipelineName = pipelineName;
		this.componentName = componentName;
		this.config = new HashMap<>();
		inputConfig.forEach((key, value) -> this.config.put(removeCasing(key), value));
	}

	public String getPipelineName() {
		return pipelineName;
	}

	public void setPipelineName(String pipelineName) {
		this.pipelineName = pipelineName;
	}

	public Map<String, String> getConfig() {
		return config;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getProperty(String key) {
		String caseInsensitiveKey = removeCasing(key);
		String value = config.get(caseInsensitiveKey);
		if (value == null) {
			throw new ConfigPropertyMissingException(pipelineName, componentName, key);
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

		Path path;
		try {
			path = Path.of(file);
		} catch (InvalidPathException e) {
			return Optional.empty();
		}

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

	/**
	 * Returns a new map with the nested properties for a provided key.
	 * For example the following config:
	 *
	 * <pre>
	 * adapter:
	 *    name: my-adapter
	 * 	  config:
	 * 	      core-context: context.ttl
	 * 	      alt-context: alt-context.ttl
	 * </pre>
	 *
	 * <br>
	 * <br>
	 * Will result in the following component properties:
	 *
	 * <pre>
	 *     adapter.name: my-adapter
	 *     adapter.config.core-context: context.ttl
	 *     adapter.config.alt-context: alt-context.ttl
	 * </pre>
	 *
	 * When providing the key "adapter.config" to this method, new component
	 * properties are returned:
	 *
	 * <pre>
	 *     core-context: context.ttl
	 *     alt-context: alt-context.ttl
	 * </pre>
	 */
	public ComponentProperties extractNestedProperties(String key) {
		if (isBlank(key)) {
			return new ComponentProperties("%s.%s".formatted(componentName, key), pipelineName);
		}

		var mappedProperties = config
				.entrySet()
				.stream()
				.filter(entry -> entry.getKey().startsWith(key))
				.filter(entry -> !key.equals(entry.getKey()))
				.collect(Collectors.toMap(
						entry -> entry.getKey().substring(key.length() + 1),
						Map.Entry::getValue));

		return new ComponentProperties(pipelineName, "%s.%s".formatted(componentName, key), mappedProperties);
	}

	private String removeCasing(String key) {
		return key
				.toLowerCase()
				.replace("-", "")
				.replace("_", "");
	}
}
