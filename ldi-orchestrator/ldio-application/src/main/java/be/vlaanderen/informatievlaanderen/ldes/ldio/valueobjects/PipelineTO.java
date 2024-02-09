package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;

import java.util.List;
import java.util.Map;

public record PipelineTO(String name, PipelineStatus status, String description, InputComponentDefinitionTO input,
                         List<ComponentDefinitionTO> transformers, List<ComponentDefinitionTO> outputs) {
	public static PipelineTO fromPipelineConfig(PipelineConfig config, PipelineStatus status) {
		var input = new InputComponentDefinitionTO(config.getInput().getName(),
				new ComponentDefinitionTO(config.getInput().getAdapter().getName(), config.getInput().getAdapter().getConfigMap()),
				config.getInput().getConfigMap());
		var transformers = config.getTransformers().stream().map(componentDefinition -> new ComponentDefinitionTO(componentDefinition.getName(), componentDefinition.getConfigMap())).toList();
		var outputs = config.getOutputs().stream().map(componentDefinition -> new ComponentDefinitionTO(componentDefinition.getName(), componentDefinition.getConfigMap())).toList();
		return new PipelineTO(config.getName(), status, config.getDescription(), input, transformers, outputs);
	}


	public record InputComponentDefinitionTO(String name, ComponentDefinitionTO adapter, Map<String, String> config) {
	}

	public record ComponentDefinitionTO(String name, Map<String, String> config) {
	}
}
