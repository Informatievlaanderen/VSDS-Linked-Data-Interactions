package be.vlaanderen.informatievlaanderen.ldes.ldio;

public class LdioLdesClientProperties {

	private LdioLdesClientProperties() {
	}

	// general properties
	public static final String URLS = "urls";
	public static final String SOURCE_FORMAT = "source-format";

	public static final String STATE = "state";
	public static final String KEEP_STATE = "keep-state";
	public static final String POSTGRES_USERNAME = "postgres.username";
	public static final String POSTGRES_PASSWORD = "postgres.password";
	public static final String POSTGRES_URL = "postgres.url";
	public static final String TIMESTAMP_PATH_PROP = "timestamp-path";
	public static final String USE_EXACTLY_ONCE_FILTER = "enable-exactly-once";

	// version materialisation properties
	public static final String USE_VERSION_MATERIALISATION = "materialisation.enabled";
	public static final String VERSION_OF_PROPERTY = "materialisation.version-of-property";

}
