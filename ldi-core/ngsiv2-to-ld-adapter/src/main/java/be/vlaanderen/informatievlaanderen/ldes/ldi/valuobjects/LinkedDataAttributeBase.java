package be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.properties.LinkedDataAttribute;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.NgsiV2ToLdMapping.translateKey;

/**
 * This class serves as a base for any properties in the transformation process.
 * Any attributes that a subclass does not catch in its constructor are caught
 * here.
 *
 */
public abstract class LinkedDataAttributeBase {

	protected Map<String, LinkedDataAttribute> properties;

	protected LinkedDataAttributeBase() {
		this.properties = new HashMap<>();
	}

	@JsonAnySetter
	public void add(String key, LinkedDataAttribute value) {

		properties.put(translateKey(key), value);
	}

	@JsonAnyGetter
	public Map<String, LinkedDataAttribute> getProperties() {

		return properties;
	}
}
