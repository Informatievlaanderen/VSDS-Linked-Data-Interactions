package be.vlaanderen.informatievlaanderen.ldes.ldi.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.NgsiV2ToLdMapping;
import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.InvalidNgsiLdContextException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.LinkedDataAttribute;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.LinkedDataModel;
import org.apache.jena.atlas.json.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.impl.StatementImpl;

import java.util.Map.Entry;

public class NgsiV2ToLdTranslatorService {

	private final String coreContext;
	private final String ldContext;

	public NgsiV2ToLdTranslatorService(String coreContext) {
		this(coreContext, null);
	}

	public NgsiV2ToLdTranslatorService(String coreContext, String ldContext) {
		this.coreContext = coreContext;
		this.ldContext = ldContext;
	}

	public LinkedDataModel translate(String data) {
		return translate(data, ldContext);
	}

	public LinkedDataModel translateNew(String data, String ldContext) {
		JsonObject parsedData = JSON.parse(data);
		LinkedDataModel model = new LinkedDataModel();
		Model jenaModel = ModelFactory.createDefaultModel();

		if (coreContext == null) {
			throw new InvalidNgsiLdContextException("Core context can't be null");
		}
		model.addContextDeclaration(coreContext);

		if (ldContext != null) {
			model.addContextDeclaration(ldContext);
		}






		return model;
	}
	public LinkedDataModel translate(String data, String ldContext) {
		JsonObject parsedData = JSON.parse(data);
		LinkedDataModel model = new LinkedDataModel();
		Model jenaModel = ModelFactory.createDefaultModel();

		if (coreContext == null) {
			throw new InvalidNgsiLdContextException("Core context can't be null");
		}
		model.addContextDeclaration(coreContext);

		if (ldContext != null) {
			model.addContextDeclaration(ldContext);
		}

		String dateObserved = parsedData.get(NgsiV2ToLdMapping.NGSI_V2_KEY_DATE_OBSERVED) != null
				? parsedData.get(NgsiV2ToLdMapping.NGSI_V2_KEY_DATE_OBSERVED).getAsObject().get(
				NgsiV2ToLdMapping.NGSI_V2_KEY_VALUE).getAsString().value()
				: null;

		//addBaseData(parsedData, jenaModel);

		for (Entry<String, JsonValue> entry : parsedData.entrySet()) {

			JsonObject objectAttribute = entry.getValue().getAsObject();

			LinkedDataAttribute modelAttribute = new LinkedDataAttribute();
			String attributeType = objectAttribute.get(NgsiV2ToLdMapping.NGSI_V2_KEY_TYPE) != null
					? objectAttribute.getString(NgsiV2ToLdMapping.NGSI_V2_KEY_TYPE)
					: NgsiV2ToLdMapping.NGSI_LD_ATTRIBUTE_TYPE_PROPERTY;

			String key = entry.getKey();
			JsonValue attribute = entry.getValue();
			// PROPERTY ATTRIBUTE
			if (!attributeType.equalsIgnoreCase(NgsiV2ToLdMapping.NGSI_LD_ATTRIBUTE_TYPE_RELATIONSHIP)) {

				modelAttribute.setValue(objectAttribute.get(NgsiV2ToLdMapping.NGSI_V2_KEY_VALUE));
				if (key.equalsIgnoreCase(NgsiV2ToLdMapping.NGSI_V2_KEY_LOCATION)) {
					modelAttribute.setType(NgsiV2ToLdMapping.NGSI_LD_ATTRIBUTE_TYPE_GEOPROPERTY);
				} else {
					modelAttribute.setType(NgsiV2ToLdMapping.NGSI_LD_ATTRIBUTE_TYPE_PROPERTY);
				}

				if (dateObserved != null) {
					modelAttribute.setDateObserved(normaliseDate(dateObserved));
				}


			}
			// RELATIONSHIP ATTRIBUTE
			else {
				modelAttribute.setType(NgsiV2ToLdMapping.NGSI_LD_ATTRIBUTE_TYPE_RELATIONSHIP);

				if (objectAttribute.get(NgsiV2ToLdMapping.NGSI_V2_KEY_VALUE).isArray()) {
					JsonArray modelAttributeObject = new JsonArray();
					JsonArray items = objectAttribute.get(NgsiV2ToLdMapping.NGSI_V2_KEY_VALUE).getAsArray();
					for (JsonValue item : items) {
						modelAttributeObject.add(translateObject(key, item.getAsString().value()));
					}

					modelAttribute.setObjectValue(modelAttributeObject);
				} else {
					modelAttribute.setObjectValue(new JsonString(
							translateObject(key,
									objectAttribute.get(NgsiV2ToLdMapping.NGSI_V2_KEY_VALUE).getAsString().value())));
				}
			}

			JsonObject attributeData = new JsonObject();
			if (attributeType.equalsIgnoreCase(NgsiV2ToLdMapping.NGSI_LD_ATTRIBUTE_TYPE_DATETIME)) {
				attributeData.put(
						NgsiV2ToLdMapping.NGSI_LD_OBJECT_TYPE, NgsiV2ToLdMapping.NGSI_LD_ATTRIBUTE_TYPE_DATETIME);
				attributeData.put(NgsiV2ToLdMapping.NGSI_LD_OBJECT_VALUE, normaliseDate(objectAttribute.getString(
						NgsiV2ToLdMapping.NGSI_V2_KEY_VALUE)));

				modelAttribute.setValue(attributeData);
			} else if (attributeType.equalsIgnoreCase(NgsiV2ToLdMapping.NGSI_LD_ATTRIBUTE_TYPE_POSTAL_ADDRESS)) {
				attributeData.put(
						NgsiV2ToLdMapping.NGSI_LD_OBJECT_TYPE, NgsiV2ToLdMapping.NGSI_LD_ATTRIBUTE_TYPE_POSTAL_ADDRESS);
				modelAttribute.setObjectValue(attributeData);
			}

			JsonObject metadata = objectAttribute.get(NgsiV2ToLdMapping.NGSI_V2_KEY_METADATA) != null
					? objectAttribute.get(NgsiV2ToLdMapping.NGSI_V2_KEY_METADATA).getAsObject()
					: null;
			if (metadata != null) {
				for (Entry<String, JsonValue> metadataEntry : metadata.entrySet()) {
					String metadataKey = metadataEntry.getKey();
					JsonValue metadataValue = metadataEntry.getValue();

					String metadataPropertyValue = "";
					if (metadataValue.isObject()
							&& metadataValue.getAsObject().get(NgsiV2ToLdMapping.NGSI_V2_KEY_VALUE) != null) {
						JsonValue metadataPropertyJsonValue = metadataValue.getAsObject().get(
								NgsiV2ToLdMapping.NGSI_V2_KEY_VALUE);
						if (metadataPropertyJsonValue.isString()) {
							metadataPropertyValue = metadataPropertyJsonValue.getAsString().value();
						}
						if (metadataPropertyJsonValue.isNumber()) {
							metadataPropertyValue = metadataPropertyJsonValue.getAsNumber().toString();
						}
					}

					if (metadataKey.equalsIgnoreCase(NgsiV2ToLdMapping.NGSI_V2_KEY_TIMESTAMP)) {
						if (dateObserved == null) {
							modelAttribute.setTimestamp(normaliseDate(
									metadataValue.getAsObject().getString(NgsiV2ToLdMapping.NGSI_V2_KEY_VALUE)));
						}
					} else if (metadataKey.equalsIgnoreCase(NgsiV2ToLdMapping.NGSI_V2_KEY_UNIT_CODE)) {
						modelAttribute.setUnitCode(metadataPropertyValue);
					} else {
						JsonObject metadataProperty = new JsonObject();

						metadataProperty.put(
								NgsiV2ToLdMapping.NGSI_LD_ATTRIBUTE_TYPE,
								NgsiV2ToLdMapping.NGSI_LD_ATTRIBUTE_TYPE_PROPERTY);
						metadataProperty.put(NgsiV2ToLdMapping.NGSI_LD_ATTRIBUTE_VALUE, metadataPropertyValue);

						modelAttribute.addMetadata(metadataKey, metadataProperty);
					}
				}
			}

			model.addAttribute(key, modelAttribute);
		}

		return model;
	}

	private void addBaseData(JsonObject parsedData, Model model) {
		String id = parsedData.get(NgsiV2ToLdMapping.NGSI_V2_KEY_ID).getAsString().value();
		String type = parsedData.get(NgsiV2ToLdMapping.NGSI_V2_KEY_TYPE).getAsString().value();


		for (Entry<String, JsonValue> entry : parsedData.entrySet()) {
			String key = entry.getKey();
			JsonValue attribute = entry.getValue();

			if (key.equalsIgnoreCase(NgsiV2ToLdMapping.NGSI_V2_KEY_ID)) {
				model.createStatement()
				model.setId(translateId(id, type));
			} else if (key.equalsIgnoreCase(NgsiV2ToLdMapping.NGSI_V2_KEY_TYPE)) {
				model.setType(type);
			} else if (key.equalsIgnoreCase(NgsiV2ToLdMapping.NGSI_V2_KEY_DATE_CREATED)) {
				model.setDateCreated(normaliseDate(attribute.getAsString().value()));
			} else if (key.equalsIgnoreCase(NgsiV2ToLdMapping.NGSI_V2_KEY_DATE_MODIFIED)) {
				model.setDateModified(normaliseDate(attribute.getAsString().value()));
			}
		}
	}

	private String translateData(String key, String data) {
		if (key.equalsIgnoreCase(NgsiV2ToLdMapping.NGSI_V2_KEY_ID)) {
			return data;
		} else if (key.equalsIgnoreCase(NgsiV2ToLdMapping.NGSI_V2_KEY_TYPE)) {
			return data;
		} else if (key.equalsIgnoreCase(NgsiV2ToLdMapping.NGSI_V2_KEY_DATE_CREATED)) {
			return normaliseDate(data);
		} else if (key.equalsIgnoreCase(NgsiV2ToLdMapping.NGSI_V2_KEY_DATE_MODIFIED)) {
			return normaliseDate(data);
		}

		return data;
	}



	private String translateId(String entityId, String entityType) {
		return NgsiLdURIParser.toNgsiLdUri(entityId, entityType);
	}

	private String translateObject(String entityId, String value) {
		return NgsiLdURIParser.toNgsiLdObjectUri(entityId, value);
	}

	private String normaliseDate(String date) {
		return NgsiLdDateParser.normaliseDate(date);
	}
}
