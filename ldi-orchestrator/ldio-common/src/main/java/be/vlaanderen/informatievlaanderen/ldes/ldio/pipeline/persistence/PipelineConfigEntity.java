package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentDefinition;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.InputComponentDefinition;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.List;

@Entity(name = "pipelines")
public class PipelineConfigEntity {
	@Id
	@Column(nullable = false)
	private String name;

	private String description;

	@Convert(converter = JsonConverter.class)
	private InputComponentDefinition input;

	@Convert(converter = JsonConverter.class)
	private List<ComponentDefinition> transformers;

	@Convert(converter = JsonConverter.class)
	private List<ComponentDefinition> outputs;

	public static PipelineConfigEntity fromConfig(PipelineConfig config) {
		var entity = new PipelineConfigEntity();
		entity.description = config.getDescription();
		entity.name = config.getName();
		entity.input = config.getInput();
		entity.transformers = config.getTransformers();
		entity.outputs = config.getOutputs();
		return entity;
	}
}
