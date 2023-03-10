package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentDefinition;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.InputComponentDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "orchestrator")
public class OrchestratorConfig {
	private InputComponentDefinition input;
	private List<ComponentDefinition> transformers = new LinkedList<>();
	private List<ComponentDefinition> outputs;

	public InputComponentDefinition getInput() {
		return input;
	}

	public void setInput(InputComponentDefinition input) {
		this.input = input;
	}

	public List<ComponentDefinition> getTransformers() {
		return transformers;
	}

	public void setTransformers(List<ComponentDefinition> transformers) {
		this.transformers = transformers;
	}

	public List<ComponentDefinition> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<ComponentDefinition> outputs) {
		this.outputs = outputs;
	}

	@Override
	public String toString() {
		return "OrchestratorConfig{" +
				"input=" + input +
				", transformers=" + transformers +
				", outputs=" + outputs +
				'}';
	}
}
