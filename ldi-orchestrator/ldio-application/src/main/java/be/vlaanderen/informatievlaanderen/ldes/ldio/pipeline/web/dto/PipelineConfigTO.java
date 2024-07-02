package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.web.dto;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.ComponentDefinition;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.InputComponentDefinition;

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

	/**
	 * Maps a pipeline config to a data transfer object
	 *
	 * @param config the pipeline config to be mapped to a TO
	 * @return the data transfer object
	 */
	public static PipelineConfigTO fromPipelineConfig(PipelineConfig config) {
		var adapter = config.getInput().getAdapter() == null ? null :
				new ComponentDefinitionTO(config.getInput().getAdapter().getName(), config.getInput().getAdapter().getConfigMap());

		var input = new InputComponentDefinitionTO(config.getInput().getName(), adapter, config.getInput().getConfigMap());
		var transformers = config.getTransformers().stream().map(componentDefinition -> new ComponentDefinitionTO(componentDefinition.getName(), componentDefinition.getConfigMap())).toList();
		var outputs = config.getOutputs().stream().map(componentDefinition -> new ComponentDefinitionTO(componentDefinition.getName(), componentDefinition.getConfigMap())).toList();
		return new PipelineConfigTO(config.getName(), config.getDescription(), input, transformers, outputs);
	}

	/**
	 * Maps the instance of the data transfer object to a pipeline config object
	 *
	 * @return the pipeline config
	 */
	public PipelineConfig toPipelineConfig() {
		var config = new PipelineConfig();
		config.setName(name);
		config.setDescription(description);
		var adapter = input.getAdapter().map(adapterTO -> new ComponentDefinition(name, adapterTO.name(), adapterTO.config())).orElse(null);
		config.setInput(new InputComponentDefinition(name, input.getName(), input.getConfig(), adapter));
		config.setTransformers(transformers.stream().map(componentDefinitionTO -> componentDefinitionTO.toComponentDefinition(name)).toList());
		config.setOutputs(outputs.stream().map(componentDefinitionTO -> componentDefinitionTO.toComponentDefinition(name)).toList());
		return config;
	}

}
