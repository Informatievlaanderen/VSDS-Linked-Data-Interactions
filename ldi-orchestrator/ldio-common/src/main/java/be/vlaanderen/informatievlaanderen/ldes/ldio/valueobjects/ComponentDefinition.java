package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import java.util.Map;

public class ComponentDefinition {
	private String pipelineName;
	private String name;
	private ComponentProperties config = new ComponentProperties(name, pipelineName);

	public ComponentDefinition(String pipelineName, String name, Map<String, String> config) {
		this.pipelineName = pipelineName;
		this.name = name;
		this.config = new ComponentProperties(pipelineName, name, config);
	}

	public ComponentDefinition() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPipelineName(String pipelineName) {
		this.pipelineName = pipelineName;
	}

	public ComponentProperties getConfig() {
		return config;
	}

	public Map<String, String> getConfigMap() {
		return config.getConfig();
	}

	public void setConfig(Map<String, String> config) {
		this.config = new ComponentProperties(pipelineName, name, config);
	}

	public void setConfig(ComponentProperties config) {
		this.config = config;
	}
}
