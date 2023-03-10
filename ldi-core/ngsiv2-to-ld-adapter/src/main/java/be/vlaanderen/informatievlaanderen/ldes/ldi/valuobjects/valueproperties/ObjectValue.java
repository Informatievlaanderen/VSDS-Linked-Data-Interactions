package be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.valueproperties;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.SerializationToJsonException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.LinkedDataAttributeBase;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.NgsiV2ToLdMapping.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObjectValue extends LinkedDataAttributeBase {

	protected String type;
	protected String value;

	@JsonCreator
	public ObjectValue(String value) {
		this.type = NGSI_LD_ATTRIBUTE_TYPE_PROPERTY;
		this.value = value;
	}

	@JsonCreator
	public ObjectValue(@JsonProperty(NGSI_V2_KEY_VALUE) String value,
			@JsonProperty(NGSI_V2_KEY_TYPE) String type) {
		this(value);
	}

	@JsonGetter(NGSI_LD_OBJECT_TYPE)
	public String getType() {
		return type;
	}

	@JsonGetter(NGSI_LD_OBJECT_VALUE)
	public String getValue() {
		return value;
	}

	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new SerializationToJsonException(e, this.value);
		}
	}
}
