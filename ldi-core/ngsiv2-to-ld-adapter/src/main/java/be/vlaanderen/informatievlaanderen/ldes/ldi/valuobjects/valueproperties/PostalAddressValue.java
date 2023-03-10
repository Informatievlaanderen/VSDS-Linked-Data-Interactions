package be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.valueproperties;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.NgsiV2ToLdMapping.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostalAddressValue extends ObjectValue {

	@JsonCreator
	public PostalAddressValue(@JsonProperty(NGSI_V2_KEY_VALUE) String value,
			@JsonProperty(NGSI_V2_KEY_TYPE) String type) {
		this(value);
	}

	@JsonCreator
	public PostalAddressValue(String value) {
		super(value);
		this.type = NGSI_LD_ATTRIBUTE_TYPE_POSTAL_ADDRESS;
	}
}
