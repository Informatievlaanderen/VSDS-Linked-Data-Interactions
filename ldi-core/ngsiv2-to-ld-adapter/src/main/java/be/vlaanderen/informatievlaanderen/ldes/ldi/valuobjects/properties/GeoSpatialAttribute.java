package be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.properties;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.NgsiV2ToLdMapping.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeoSpatialAttribute extends LinkedDataAttribute {

	@JsonCreator
	public GeoSpatialAttribute(@JsonProperty(NGSI_V2_KEY_TYPE) String type,
			@JsonProperty(NGSI_V2_KEY_VALUE) Object value,
			@JsonProperty(NGSI_V2_KEY_DATE_OBSERVED) String dateObserved,
			@JsonProperty(NGSI_V2_KEY_METADATA) MetaDataAttribute metaData) {
		super(type, value, dateObserved, metaData);
		this.type = NGSI_LD_ATTRIBUTE_TYPE_GEOPROPERTY;
	}

}
