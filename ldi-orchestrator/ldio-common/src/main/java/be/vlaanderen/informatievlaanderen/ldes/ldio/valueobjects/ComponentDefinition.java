package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import java.io.Serializable;
import java.util.Map;

public class ComponentDefinition implements Serializable {
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

	public void setConfig(Map<String, String> config) {
		this.config = new ComponentProperties(config);
	}
}
