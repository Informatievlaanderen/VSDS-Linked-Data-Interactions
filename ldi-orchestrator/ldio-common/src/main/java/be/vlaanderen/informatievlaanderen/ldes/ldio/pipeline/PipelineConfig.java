package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentDefinition;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.InputComponentDefinition;

import java.util.LinkedList;
import java.util.List;

public class PipelineConfig {
	public static final String NAME_PATTERN = "^[0-9a-zA-Z_\\-. ]+$";
	public static final String PIPELINE_NAME = "pipeline.name";
	private String name;
	private String description = "";
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
