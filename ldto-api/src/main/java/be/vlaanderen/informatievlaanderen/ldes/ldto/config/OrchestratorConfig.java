package be.vlaanderen.informatievlaanderen.ldes.ldto.config;

import be.vlaanderen.informatievlaanderen.ldes.ldto.types.ComponentDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "orchestrator")
public class OrchestratorConfig {
	private ComponentDefinition input;
	private List<ComponentDefinition> transformers;
	private ComponentDefinition output;

	public ComponentDefinition getInput() {
		return input;
	}

	public void setInput(ComponentDefinition input) {
		this.input = input;
	}

	public List<ComponentDefinition> getTransformers() {
		return transformers;
	}

	public void setTransformers(List<ComponentDefinition> transformers) {
		this.transformers = transformers;
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
				", components=" + transformers +
				", output=" + output +
				'}';
	}
}
