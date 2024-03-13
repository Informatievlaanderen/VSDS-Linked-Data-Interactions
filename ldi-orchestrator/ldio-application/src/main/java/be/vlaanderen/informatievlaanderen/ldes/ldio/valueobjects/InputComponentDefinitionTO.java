package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldio.converters.FlattenDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class InputComponentDefinitionTO {
	private final String name;
	@JsonProperty
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final ComponentDefinitionTO adapter;
	@JsonDeserialize(using = FlattenDeserializer.class)
	private final Map<String, String> config;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public InputComponentDefinitionTO(
			 @JsonProperty("name") String name,
			 @JsonProperty("adapter") ComponentDefinitionTO adapter,
			 @JsonProperty("config") Map<String, String> config) {
		this.name = name;
		this.adapter = adapter;
		this.config = config == null ? Map.of() : config;
	}

	public String getName() {
		return name;
	}

	@JsonIgnore
	public Optional<ComponentDefinitionTO> getAdapter() {
		return Optional.ofNullable(adapter);
	}

	public Map<String, String> getConfig() {
		return config;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (InputComponentDefinitionTO) obj;
		return Objects.equals(this.name, that.name) &&
				Objects.equals(this.adapter, that.adapter) &&
				Objects.equals(this.config, that.config);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, adapter, config);
	}

	@Override
	public String toString() {
		return "InputComponentDefinitionTO[" +
				"name=" + name + ", " +
				"adapter=" + adapter + ", " +
				"config=" + config + ']';
	}

}
