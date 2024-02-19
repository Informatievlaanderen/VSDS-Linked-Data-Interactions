package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;

import java.util.List;

public record PipelineConfigTO(String name, String description, InputComponentDefinitionTO input,
                               List<ComponentDefinitionTO> transformers, List<ComponentDefinitionTO> outputs) {
	public PipelineConfigTO(String name, String description, InputComponentDefinitionTO input,
	                        List<ComponentDefinitionTO> transformers, List<ComponentDefinitionTO> outputs) {
		this.name = name;
		this.description = description;
		this.input = input;
		this.transformers = transformers == null ? List.of() : transformers;
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

	public PipelineConfig toPipelineConfig() {
		var config = new PipelineConfig();
		config.setName(name);
		config.setDescription(description);
		var adapter = input().adapter() == null ? null : new ComponentDefinition(name, input.adapter().name(), input.adapter().config());
		config.setInput(new InputComponentDefinition(name, input.name(), input.config(), adapter));
		config.setTransformers(transformers.stream().map(componentDefinitionTO -> componentDefinitionTO.toComponentDefinition(name)).toList());
		config.setOutputs(outputs.stream().map(componentDefinitionTO -> componentDefinitionTO.toComponentDefinition(name)).toList());
		return config;
	}

}
