package be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.NgsiV2ToLdMapping;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonString;
import org.apache.jena.atlas.json.JsonValue;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.NgsiV2ToLdMapping.*;


public class LinkedDataModel {

	private final JsonObject jsonModel;
	private JsonModel jsonModelNew;
	private final JsonArray contexts;

	public LinkedDataModel() {
		jsonModel = new JsonObject();
		contexts = new JsonArray();
	}

	public JsonObject getModel() {
		jsonModel.put(NGSI_LD_CONTEXT, contexts);

		return jsonModel;
	}

	public JsonModel getJsonModelNew() {
		return jsonModelNew;
	}

	public String toString() {
		return getJsonModelNew().toString();
	}

	public Model toRDFModel(Lang format) {
		Model model = ModelFactory.createDefaultModel();
		RDFParser.fromString(toString())
				.lang(format)
				.parse(model);
		return model;
	}

	public List<String> getContexts() {
		return contexts.stream().map(JsonValue::getAsString).map(JsonString::value).toList();
	}

	public void addContextDeclaration(String context) {
		JsonString jsonContext = new JsonString(context);
		if (!contexts.contains(jsonContext)) {
			contexts.add(jsonContext);
		}
	}

	public void addContexts(List<String> contexts) {
		this.contexts.clear();
		this.contexts.addAll(contexts.stream()
				.map(JsonString::new)
				.toList());
	}

	public void setId(String entityId) {
		jsonModel.put(translateKey(NGSI_V2_KEY_ID), new JsonString(entityId));
	}

	public void setType(String entityType) {
		jsonModel.put(translateKey(NGSI_V2_KEY_TYPE), new JsonString(entityType));
	}

	public void setDateCreated(String dateCreated) {
		jsonModel.put(translateKey(NgsiV2ToLdMapping.NGSI_V2_KEY_DATE_CREATED), dateCreated);
	}

	public void setDateModified(String dateModified) {
		jsonModel.put(translateKey(NGSI_V2_KEY_DATE_MODIFIED), dateModified);
	}

	public void setDateObserved(String dateObserved) {
		jsonModel.put(NGSI_V2_KEY_DATE_OBSERVED, dateObserved);
	}

	public boolean hasDateObserved() {
		return jsonModel.hasKey(NGSI_V2_KEY_DATE_OBSERVED) && !jsonModel.get(NGSI_V2_KEY_DATE_OBSERVED).isNull();
	}

	public void addAttribute(String attributeKey, LinkedDataAttribute attribute) {
		jsonModel.put(translateKey(attributeKey), attribute.toAttribute());
	}
}
