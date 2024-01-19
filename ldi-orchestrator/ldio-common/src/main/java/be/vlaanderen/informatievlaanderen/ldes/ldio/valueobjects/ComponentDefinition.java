package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import java.util.Map;

public class ComponentDefinition {

	public ComponentDefinition(String name, Map<String, String> config) {
		this.name = name;
		this.config = new ComponentProperties(config);
	}

	public ComponentDefinition() {
	}

	private String name;
	private ComponentProperties config = new ComponentProperties();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ComponentProperties getConfig() {
		return config;
	}

	public Map<String, String> getConfigMap() {
		return config.getConfig();
	}

	public void setConfig(Map<String, String> config) {
		this.config = new ComponentProperties(config);
	}
}
