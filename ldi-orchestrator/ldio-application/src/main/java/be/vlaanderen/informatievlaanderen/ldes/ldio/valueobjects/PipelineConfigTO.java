package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;

import java.util.List;
import java.util.Map;

public class PipelineConfigTO {
	public String name;
	public String description;
	public InputComponentDefinitionTO input;
	public List<ComponentDefinitionTO> transformers;
	public List<ComponentDefinitionTO> outputs;

	public PipelineConfigTO(String name, String description, InputComponentDefinitionTO input,
	                        List<ComponentDefinitionTO> transformers, List<ComponentDefinitionTO> outputs) {
		this.name = name;
		this.description = description;
		this.input = input;
		this.transformers = transformers;
		this.outputs = outputs;
	}

	public static PipelineConfigTO fromPipelineConfig(PipelineConfig config) {
		var input = new InputComponentDefinitionTO(config.getInput().getName(),
				new ComponentDefinitionTO(config.getInput().getAdapter().getName(), config.getInput().getAdapter().getConfigMap()),
				config.getInput().getConfigMap());
		var transformers = config.getTransformers().stream().map(componentDefinition -> new ComponentDefinitionTO(componentDefinition.getName(), componentDefinition.getConfigMap())).toList();
		var outputs = config.getOutputs().stream().map(componentDefinition -> new ComponentDefinitionTO(componentDefinition.getName(), componentDefinition.getConfigMap())).toList();
		return new PipelineConfigTO(config.getName(), config.getDescription(), input, transformers, outputs);
	}


	public record InputComponentDefinitionTO(String name, ComponentDefinitionTO adapter, Map<String, String> config) {
	}

	public record ComponentDefinitionTO(String name, Map<String, String> config) {
	}
}
