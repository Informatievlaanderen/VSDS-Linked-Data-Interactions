package be.vlaanderen.informatievlaanderen.ldes.ldto.types;

import java.util.Map;

public class ComponentDefinition {
	private String name;
	private Map<String, String> config;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getConfig() {
		return config;
	}

	public void setConfig(Map<String, String> config) {
		this.config = config;
	}

	@Override public String toString() {
		return "ComponentDefinition{" +
				"name='" + name + '\'' +
				", config=" + config +
				'}';
	}
}
