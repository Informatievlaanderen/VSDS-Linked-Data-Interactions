package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;

public class EdcToken {

    private final JsonObject token;

    public EdcToken(JsonObject token) {
        this.token = token;
    }

    public static EdcToken fromJsonString(String token) {
        return new EdcToken(JSON.parse(token));
    }

    public RequestHeader getTokenHeader() {
        final String authKey = token.get("authKey").getAsString().value();
        final String authCode = token.get("authCode").getAsString().value();
        return new RequestHeader(authKey, authCode);
    }

}
