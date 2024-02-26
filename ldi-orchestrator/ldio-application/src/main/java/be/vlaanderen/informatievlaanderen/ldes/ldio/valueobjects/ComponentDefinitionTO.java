package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldio.converters.FlattenDeserializer;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
