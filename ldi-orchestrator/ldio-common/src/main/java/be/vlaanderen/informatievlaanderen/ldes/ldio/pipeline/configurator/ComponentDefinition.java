package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator;

import java.util.Map;

public class ComponentDefinition {

	private final String name;
	private final ComponentProperties config;

	public ComponentDefinition(String pipelineName, String name, Map<String, String> config) {
		this.name = name;
		this.config = config != null
				? new ComponentProperties(pipelineName, name, config)
				: new ComponentProperties(pipelineName, name);
	}

	public String getName() {
		return name;
	}

	public ComponentProperties getConfig() {
		return config;
	}

	public Map<String, String> getConfigMap() {
		return config.getConfig();
	}

}
