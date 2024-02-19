package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import java.util.List;

public record PipelineTO(String name, PipelineStatus status, String description, InputComponentDefinitionTO input,
                         List<ComponentDefinitionTO> transformers, List<ComponentDefinitionTO> outputs) {
	public static PipelineTO fromPipelineConfig(PipelineConfigTO config, PipelineStatus status) {
		var input = new InputComponentDefinitionTO(config.input().name(),
				new ComponentDefinitionTO(config.input().adapter().name(), config.input().adapter().config()),
				config.input().config());
		List<ComponentDefinitionTO> transformers;
		if (config.transformers() == null) {
			transformers = List.of();
		} else {
			transformers = config.transformers().stream().map(componentDefinition -> new ComponentDefinitionTO(componentDefinition.name(), componentDefinition.config())).toList();
		}
		var outputs = config.outputs().stream().map(componentDefinition -> new ComponentDefinitionTO(componentDefinition.name(), componentDefinition.config())).toList();
		return new PipelineTO(config.name(), status, config.description(), input, transformers, outputs);
	}
}
