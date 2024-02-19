package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldio.converters.FlattenDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

public record InputComponentDefinitionTO(String name, ComponentDefinitionTO adapter,
                                         @JsonDeserialize(using = FlattenDeserializer.class) Map<String, String> config) {
	public InputComponentDefinitionTO(String name, ComponentDefinitionTO adapter, Map<String, String> config) {
		this.name = name;
		this.adapter = adapter;
		this.config = config == null ? Map.of() : config;
	}
}