package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;

import java.util.List;
import java.util.Map;

public record PipelineConfigTO(String name, String description, InputComponentDefinitionTO input,
                               List<ComponentDefinitionTO> transformers, List<ComponentDefinitionTO> outputs) {
	public static PipelineConfigTO fromPipelineConfig(PipelineConfig config) {
		var input = new InputComponentDefinitionTO(config.getInput().getName(),
				new ComponentDefinitionTO(config.getInput().getAdapter().getName(), config.getInput().getAdapter().getConfigMap()),
				config.getInput().getConfigMap());
		var transformers = config.getTransformers().stream().map(componentDefinition -> new ComponentDefinitionTO(componentDefinition.getName(), componentDefinition.getConfigMap())).toList();
		var outputs = config.getOutputs().stream().map(componentDefinition -> new ComponentDefinitionTO(componentDefinition.getName(), componentDefinition.getConfigMap())).toList();
		return new PipelineConfigTO(config.getName(), config.getDescription(), input, transformers, outputs);
	}

	public PipelineConfig toPipelineConfig() {
		var config = new PipelineConfig();
		config.setName(name);
		config.setDescription(description);
		config.setInput(new InputComponentDefinition(input.name, input.config,
				new ComponentDefinition(input.adapter.name, input.adapter.config)));
		config.setTransformers(transformers.stream().map(ComponentDefinitionTO::toComponentDefinition).toList());
		config.setOutputs(outputs.stream().map(ComponentDefinitionTO::toComponentDefinition).toList());
		return config;
	}

	public record InputComponentDefinitionTO(String name, ComponentDefinitionTO adapter, Map<String, String> config) {
	}

	public record ComponentDefinitionTO(String name, Map<String, String> config) {
		ComponentDefinition toComponentDefinition() {
			return new ComponentDefinition(name, config);
		}
	}
}
