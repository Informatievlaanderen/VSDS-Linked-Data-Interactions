package be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.SerializationToJsonException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.properties.LinkedDataAttribute;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.valueproperties.DateTimeValue;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.NgsiV2ToLdMapping.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.services.NgsiLdURIParser.toNgsiLdUri;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkedDataModel extends LinkedDataAttributeBase {

	private List<String> contexts;
	private String id;
	private String type;
	private DateTimeValue dateCreated;
	private DateTimeValue dateModified;
	private DateTimeValue dateObserved;

	public LinkedDataModel() {
		super();
		this.contexts = new ArrayList<>();
	}

	// deserialize from NGSI_V2
	@JsonCreator
	public LinkedDataModel(@JsonProperty(NGSI_V2_KEY_ID) String id,
			@JsonProperty(NGSI_V2_KEY_TYPE) String type,
			@JsonProperty(NGSI_V2_KEY_DATE_OBSERVED) DateTimeValue dateObserved,
			@JsonProperty(NGSI_V2_KEY_DATE_CREATED) DateTimeValue dateCreated,
			@JsonProperty(NGSI_V2_KEY_DATE_MODIFIED) DateTimeValue dateModified) {
		this();
		this.id = toNgsiLdUri(id, type);
		this.type = type;
		this.dateCreated = dateCreated;
		this.dateModified = dateModified;
		this.dateObserved = dateObserved;
		if (dateObserved != null) {
			add(NGSI_V2_KEY_DATE_OBSERVED,
					new LinkedDataAttribute(NGSI_V2_KEY_TIMESTAMP, dateObserved.getValue(), null, null));
		}
	}

	@Override
	@JsonAnySetter
	public void add(String key, LinkedDataAttribute value) {

		if (!Objects.equals(value.getType(), NGSI_LD_ATTRIBUTE_TYPE_RELATIONSHIP)) {
			value.setDateObserved(getDateObservedValue());
		}
		properties.put(translateKey(key), value);
	}

	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new SerializationToJsonException(e, this.id);
		}
	}

	public Model toRDFModel() {
		Model model = ModelFactory.createDefaultModel();
		RDFParser.fromString(toString())
				.lang(Lang.JSONLD)
				.parse(model);
		return model;
	}

	public void addContextDeclaration(String context) {
		if (!contexts.contains(context)) {
			contexts.add(context);
		}
	}

	public void setContexts(List<String> contexts) {
		this.contexts = contexts;
	}

	// serialize to Json-LD
	@JsonGetter(NGSI_LD_CONTEXT)
	public String[] getContextsForSerialization() {
		return contexts.toArray(new String[0]);
	}

	@JsonGetter(NGSI_LD_ID)
	public String getId() {
		return id;
	}

	@JsonGetter(NGSI_LD_ATTRIBUTE_TYPE)
	public String getType() {
		return type;
	}

	@JsonIgnore
	public List<String> getContexts() {
		return contexts;
	}

	@JsonIgnore
	public String getDateObservedValue() {
		return dateObserved == null ? null : dateObserved.getValue();
	}

	@JsonIgnore
	public DateTimeValue getDateObserved() {
		return dateObserved;
	}

	public String getValueDateCreated() {
		return dateCreated != null ? dateCreated.getValue() : null;
	}

	public String getValueDateModified() {
		return dateModified != null ? dateModified.getValue() : null;
	}

}
