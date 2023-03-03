package be.vlaanderen.informatievlaanderen.ldes.ldi.config;

import java.util.Map;

public class NgsiV2ToLdMapping {

	private NgsiV2ToLdMapping() {
	}

	public static final String NGSI_V2_KEY_ID = "id";
	public static final String NGSI_V2_KEY_TYPE = "type";
	public static final String NGSI_V2_KEY_VALUE = "value";
	public static final String NGSI_V2_KEY_LOCATION = "location";
	public static final String NGSI_V2_KEY_DATE_CREATED = "dateCreated";
	public static final String NGSI_V2_KEY_DATE_MODIFIED = "dateModified";
	public static final String NGSI_V2_KEY_DATE_OBSERVED = "dateObserved";
	public static final String NGSI_V2_KEY_METADATA = "metadata";
	public static final String NGSI_V2_KEY_TIMESTAMP = "timestamp";
	public static final String NGSI_V2_KEY_UNIT_CODE = "unitCode";
	public static final String NGSI_V2_KEY_COORDINATES = "coordinates";

	public static final String NGSI_LD_KEY_OBJECT = "object";
	public static final String NGSI_LD_KEY_DATE_CREATED = "createdAt";
	public static final String NGSI_LD_KEY_DATE_MODIFIED = "modifiedAt";
	public static final String NGSI_LD_KEY_DATE_OBSERVED = "observedAt";
	public static final String NGSI_LD_KEY_OBSERVED_AT = "observedAt";

	public static final String NGSI_LD_CONTEXT = "@context";
	public static final String NGSI_LD_ATTRIBUTE_TYPE = "type";
	public static final String NGSI_LD_ATTRIBUTE_VALUE = "value";
	public static final String NGSI_LD_OBJECT_TYPE = "@type";
	public static final String NGSI_LD_OBJECT_VALUE = "@value";
	public static final String NGSI_LD_ATTRIBUTE_TYPE_RELATIONSHIP = "Relationship";
	public static final String NGSI_LD_ATTRIBUTE_TYPE_PROPERTY = "Property";
	public static final String NGSI_LD_ATTRIBUTE_TYPE_GEOPROPERTY = "GeoProperty";
	public static final String NGSI_LD_ATTRIBUTE_TYPE_DATETIME = "DateTime";
	public static final String NGSI_LD_ATTRIBUTE_TYPE_POSTAL_ADDRESS = "PostalAddress";

	private static final Map<String, String> MAPPINGS = Map.of(
			NGSI_V2_KEY_DATE_CREATED, NGSI_LD_KEY_DATE_CREATED,
			NGSI_V2_KEY_DATE_MODIFIED, NGSI_LD_KEY_DATE_MODIFIED,
			NGSI_V2_KEY_TIMESTAMP, NGSI_LD_KEY_OBSERVED_AT);

	public static final String translateKey(String key) {
		return MAPPINGS.get(key) != null ? MAPPINGS.get(key) : key;
	}
}
