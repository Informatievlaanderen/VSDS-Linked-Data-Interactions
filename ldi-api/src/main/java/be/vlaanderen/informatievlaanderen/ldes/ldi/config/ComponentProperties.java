package be.vlaanderen.informatievlaanderen.ldes.ldi.config;

import java.util.Map;
import java.util.Optional;

public class ComponentProperties {
	private final Map<String, String> config;

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

}
