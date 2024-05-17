package be.vlaanderen.informatievlaanderen.ldes.ldio;

public class LdioLdesClientProperties {

	private LdioLdesClientProperties() {
	}

	// general properties
	public static final String URLS = "urls";
	public static final String SOURCE_FORMAT = "source-format";

	public static final String TIMESTAMP_PATH_PROP = "timestamp-path";
	public static final String USE_EXACTLY_ONCE_FILTER = "enable-exactly-once";

	// version materialisation properties
	public static final String USE_VERSION_MATERIALISATION = "materialisation.enabled";
	public static final String VERSION_OF_PROPERTY = "materialisation.version-of-property";
	public static final String USE_LATEST_STATE_FILTER = "materialisation.enable-latest-state";

}
