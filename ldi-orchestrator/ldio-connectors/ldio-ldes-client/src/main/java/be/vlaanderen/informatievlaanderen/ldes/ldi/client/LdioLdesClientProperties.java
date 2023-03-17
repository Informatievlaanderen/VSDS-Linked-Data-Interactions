package be.vlaanderen.informatievlaanderen.ldes.ldi.client;

public class LdioLdesClientProperties {
	// general properties
	public static final String URL = "url";
	public static final String SOURCE_FORMAT = "source-format";

	public static final String STATE = "state";
	public static final String KEEP_STATE = "keep-state";

	public static final String RETRIES_ENABLED = "retries.enabled";
	public static final String MAX_RETRIES = "retries.max";

	// authorization properties
	public static final String AUTH_TYPE = "auth.type";
	public static final String API_KEY = "auth.api-key";
	public static final String API_KEY_HEADER = "auth.api-key-header";
	public static final String CLIENT_ID = "auth.client-id";
	public static final String CLIENT_SECRET = "auth.client-secret";
	public static final String TOKEN_ENDPOINT = "auth.token-endpoint";
}
