package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record PipelineTO(
		String name,
		PipelineStatus status,
		StatusChangeSource updateSource,
		String description,
		InputComponentDefinitionTO input,
		List<ComponentDefinitionTO> transformers,
		List<ComponentDefinitionTO> outputs
) {
	/**
	 * Build a pipeline data transfer object based on the provided information
	 *
	 * @param config             transfer object of the pipeline configuration
	 * @param status             the current status of the pipeline
	 * @param statusChangeSource the source where the pipeline has last been updated
	 * @return a data transfer object of the pipeline
	 */
	public static PipelineTO build(PipelineConfigTO config, PipelineStatus status, StatusChangeSource statusChangeSource) {
		var input = new InputComponentDefinitionTO(config.input().getName(),
				config.input().getAdapter().map(adapter -> new ComponentDefinitionTO(adapter.name(), adapter.config())).orElse(null),
				config.input().getConfig());
		List<ComponentDefinitionTO> transformers = Optional.ofNullable(config.transformers())
				.orElse(Collections.emptyList())
				.stream()
				.map(componentDefinition -> new ComponentDefinitionTO(componentDefinition.name(), componentDefinition.config()))
				.toList();
		var outputs = config.outputs().stream()
				.map(componentDefinition -> new ComponentDefinitionTO(componentDefinition.name(), componentDefinition.config()))
				.toList();
		return new PipelineTO(config.name(), status, statusChangeSource, config.description(), input, transformers, outputs);
	}
}
