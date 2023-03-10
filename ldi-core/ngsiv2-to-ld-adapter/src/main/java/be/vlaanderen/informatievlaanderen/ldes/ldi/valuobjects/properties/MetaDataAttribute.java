package be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.properties;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.NgsiLdDateParser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.LinkedDataAttributeBase;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.valueproperties.ObjectValue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.NgsiV2ToLdMapping.*;
import static java.util.Objects.isNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetaDataAttribute extends LinkedDataAttributeBase {

	private String timestamp;
	private final ObjectValue unitCode;

	@JsonCreator
	public MetaDataAttribute(@JsonProperty(NGSI_V2_KEY_TIMESTAMP) String timestamp,
			@JsonProperty(NGSI_V2_KEY_UNIT_CODE) ObjectValue unitCode) {

		super();
		this.unitCode = unitCode;
		if (!isNull(timestamp) && !timestamp.isEmpty()) {
			this.timestamp = NgsiLdDateParser.normaliseDate(timestamp);
		}

	}

	public String getTimestamp() {
		return timestamp;
	}

	public ObjectValue getUnitCode() {
		return unitCode;
	}

	public Boolean hasTimestamp() {
		return !isNull(timestamp) && !timestamp.isEmpty();
	}

	public Boolean hasUnitCode() {
		return !isNull(unitCode);
	}
}
