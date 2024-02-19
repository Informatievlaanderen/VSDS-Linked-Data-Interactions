package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentDefinition;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.InputComponentDefinition;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PipelineConfig {
	public static final String NAME_PATTERN = "^[0-9a-zA-Z_\\-. ]+$";
	public static final String PIPELINE_NAME = "pipeline.name";
	private String name;
	private String description;
	private InputComponentDefinition input;
	private List<ComponentDefinition> transformers = new LinkedList<>();
	private List<ComponentDefinition> outputs;

	//TODO remove this with removal of Spring based config
	public void updatePipelineName() {
		input.setPipelineName(name);
		if (input.getAdapter() != null) {
			input.getAdapter().setPipelineName(name);
		}
		transformers.forEach(transformer -> transformer.setPipelineName(name));
		outputs.forEach(output -> output.setPipelineName(name));
	}

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
		return outputs.stream().map(componentDefinition -> {
			Map<String, String> properties = componentDefinition.getConfig().getConfig();
			properties.put(PIPELINE_NAME, name);
			return new ComponentDefinition(componentDefinition.getName(), properties);
		}).toList();
	}

	public void setOutputs(List<ComponentDefinition> outputs) {
		this.outputs = outputs;
	}
}
