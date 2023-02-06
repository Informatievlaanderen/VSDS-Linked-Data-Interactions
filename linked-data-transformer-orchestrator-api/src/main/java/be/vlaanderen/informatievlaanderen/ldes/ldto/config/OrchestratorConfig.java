package be.vlaanderen.informatievlaanderen.ldes.ldto.config;

import be.vlaanderen.informatievlaanderen.ldes.ldto.types.ComponentDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "orchestrator")
public class OrchestratorConfig {
	private ComponentDefinition input;
	private List<ComponentDefinition> components;
	private ComponentDefinition output;

	public ComponentDefinition getInput() {
		return input;
	}

	public void setInput(ComponentDefinition input) {
		this.input = input;
	}

	public List<ComponentDefinition> getComponents() {
		return components;
	}

	public void setComponents(List<ComponentDefinition> components) {
		this.components = components;
	}

	public ComponentDefinition getOutput() {
		return output;
	}

	public void setOutput(ComponentDefinition output) {
		this.output = output;
	}

	@Override
	public String toString() {
		return "OrchestratorConfig{" +
				"input=" + input +
				", components=" + components +
				", output=" + output +
				'}';
	}
}
