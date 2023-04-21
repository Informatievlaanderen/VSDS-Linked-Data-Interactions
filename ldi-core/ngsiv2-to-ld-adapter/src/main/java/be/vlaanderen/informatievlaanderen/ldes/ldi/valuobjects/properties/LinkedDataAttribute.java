package be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.properties;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.NgsiLdDateParser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.*;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.valueproperties.DateTimeValue;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.valueproperties.ObjectValue;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.valueproperties.PostalAddressValue;

import com.fasterxml.jackson.annotation.*;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.NgsiV2ToLdMapping.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.NgsiV2ToLdMapping.NGSI_V2_KEY_TIMESTAMP;
import static java.util.Objects.isNull;

/**
 * LinkedDataAttribute and its subclasses are used to determine how certain
 * types of properties are interpreted and transformed.
 * When using this class with a jackson object mapper, the correct subclass will
 * be assigned based on the value of the property "type".
 * When this value has no corresponding subclass, this class is assigned by
 * default.
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true, defaultImpl = LinkedDataAttribute.class)
@JsonSubTypes({
		@JsonSubTypes.Type(value = RelationshipAttribute.class, names = { NGSI_V2_TYPE_REFERENCE,
				NGSI_LD_ATTRIBUTE_TYPE_RELATIONSHIP }),
		@JsonSubTypes.Type(value = GeoSpatialAttribute.class, names = { NGSI_V2_TYPE_GEO,
				NGSI_LD_ATTRIBUTE_TYPE_GEOPROPERTY }),
		@JsonSubTypes.Type(value = LinkedDataAttribute.class)
})
public class LinkedDataAttribute extends LinkedDataAttributeBase {

	protected String type;
	protected Object value;
	protected String dateObserved;
	protected ObjectValue unitCode;

	public LinkedDataAttribute() {
		super();
	}

	@JsonCreator
	public LinkedDataAttribute(@JsonProperty(NGSI_V2_KEY_TYPE) String type,
			@JsonProperty(NGSI_V2_KEY_VALUE) Object value,
			@JsonProperty(NGSI_V2_KEY_DATE_OBSERVED) String dateObserved,
			@JsonProperty(NGSI_V2_KEY_METADATA) MetaDataAttribute metaData) {
		this();
		this.type = NGSI_LD_ATTRIBUTE_TYPE_PROPERTY;

		this.value = interpretValueProperties(type, value);

		if (dateObserved != null) {
			this.dateObserved = NgsiLdDateParser.normaliseDate(dateObserved);
		}
		extractMetadataProperties(metaData);

	}

	@JsonIgnore
	public void setDateObserved(String dateObserved) {
		this.dateObserved = dateObserved;
	}

	@JsonGetter(NGSI_LD_ATTRIBUTE_TYPE)
	public String getType() {
		return type;
	}

	@JsonGetter(NGSI_LD_KEY_DATE_OBSERVED)
	public String getDateObserved() {
		return dateObserved;
	}

	@JsonGetter(NGSI_LD_ATTRIBUTE_VALUE)
	public Object getValue() {
		return value;
	}

	@JsonGetter(NGSI_V2_KEY_UNIT_CODE)
	public String getUnitCode() {
		return unitCode == null ? null : unitCode.getValue();
	}

	private Object interpretValueProperties(String type, Object value) {
		if ((type != null) && (type.equalsIgnoreCase(NGSI_V2_KEY_TIMESTAMP)
				|| type.equalsIgnoreCase(NGSI_LD_ATTRIBUTE_TYPE_DATETIME))) {
			return value == null ? null : (new DateTimeValue((String) value));
		} else if ((type != null) && (type.equalsIgnoreCase(NGSI_LD_ATTRIBUTE_TYPE_POSTAL_ADDRESS))) {
			return value == null ? null : (new PostalAddressValue((String) value));
		} else {
			return value;
		}
	}

	private void extractMetadataProperties(MetaDataAttribute metaData) {
		if (!isNull(metaData)) {
			if (Boolean.TRUE.equals(metaData.hasTimestamp())) {
				this.dateObserved = metaData.getTimestamp();
			}
			if (Boolean.TRUE.equals(metaData.hasUnitCode())) {
				this.unitCode = metaData.getUnitCode();
			}
			this.properties.putAll(metaData.getProperties());
		}
	}

}
