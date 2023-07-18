package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentDefinition;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.InputComponentDefinition;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@RedisHash("Pipeline")
public class PipelineConfig implements Serializable {
	public static final String PIPELINE_NAME = "pipeline.name";
	@Id
	private String name;
	private String description;
	private InputComponentDefinition input;
	private List<ComponentDefinition> transformers = new LinkedList<>();
	private List<ComponentDefinition> outputs;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

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
}
