package be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.NgsiV2ToLdMapping;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.NgsiLdDateParser;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;

public class JsonModel {

    private String id;
    private String type;
    private String dateCreated;
    private String dateModified;

    private Map<String, String> properties;
    private Map<String, String> relationships;



    @JsonCreator
    public JsonModel(@JsonProperty(NgsiV2ToLdMapping.NGSI_V2_KEY_ID) String id,
                     @JsonProperty(NgsiV2ToLdMapping.NGSI_LD_ATTRIBUTE_TYPE) String type,
                     @JsonProperty(NgsiV2ToLdMapping.NGSI_LD_KEY_DATE_CREATED) String dateCreated,
                     @JsonProperty(NgsiV2ToLdMapping.NGSI_LD_KEY_DATE_MODIFIED) String dateModified) {
        this.id = id;
        this.type = type;
        this.dateCreated = normaliseDate(dateCreated);
        this.dateModified = normaliseDate(dateModified);

    }


    @JsonAnySetter
    public void add(String key, String value) {
        properties.put(key, value);
    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }


    private String normaliseDate(String date) {
        return NgsiLdDateParser.normaliseDate(date);
    }




}
