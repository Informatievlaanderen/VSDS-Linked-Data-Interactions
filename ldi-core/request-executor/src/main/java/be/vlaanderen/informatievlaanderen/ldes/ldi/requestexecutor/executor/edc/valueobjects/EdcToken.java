package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;

public class EdcToken {

	private final JsonObject token;

	private EdcToken(JsonObject token) {
		this.token = token;
	}

	public static EdcToken fromJsonString(String token) {
		return new EdcToken(JSON.parse(token));
	}

	public RequestHeader getTokenHeader() {
		final var authKey = token.get("authKey");
		if (authKey == null) {
			throw new IllegalArgumentException("Invalid token: authKey not found");
		}
		final var authCode = token.get("authCode");
		if (authCode == null) {
			throw new IllegalArgumentException("Invalid token: authCode not found");
		}
		return new RequestHeader(authKey.getAsString().value(), authCode.getAsString().value());
	}

}
