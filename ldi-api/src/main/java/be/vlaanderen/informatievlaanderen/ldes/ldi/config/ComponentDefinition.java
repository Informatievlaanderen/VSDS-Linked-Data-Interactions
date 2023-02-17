package be.vlaanderen.informatievlaanderen.ldes.ldi.config;

import java.util.Map;

public class ComponentDefinition {
	private String name;
	private ComponentProperties config;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ComponentProperties getConfig() {
		return config;
	}

	public void setConfig(Map<String, String> config) {
		this.config = new ComponentProperties(config);
	}

}
