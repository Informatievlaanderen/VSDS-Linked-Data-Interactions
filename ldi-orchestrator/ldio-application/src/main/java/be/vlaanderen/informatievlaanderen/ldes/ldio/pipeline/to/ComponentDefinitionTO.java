package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.to;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.ComponentDefinition;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.to.deserialisers.FlattenDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

public record ComponentDefinitionTO(String name,
                                    @JsonDeserialize(using = FlattenDeserializer.class) Map<String, String> config) {
	public ComponentDefinitionTO(String name, Map<String, String> config) {
		this.name = name;
		this.config = config == null ? Map.of() : config;
	}

	ComponentDefinition toComponentDefinition(String pipelineName) {
		return new ComponentDefinition(pipelineName, name, config == null ? Map.of() : config);
	}

}