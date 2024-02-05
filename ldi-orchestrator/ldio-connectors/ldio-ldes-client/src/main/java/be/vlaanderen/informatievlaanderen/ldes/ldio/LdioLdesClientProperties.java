package be.vlaanderen.informatievlaanderen.ldes.ldio;

public class LdioLdesClientProperties {

	private LdioLdesClientProperties() {
	}

	// general properties
	public static final String URL = "url";
	public static final String SOURCE_FORMAT = "source-format";

	public static final String STATE = "state";
	public static final String KEEP_STATE = "keep-state";
	public static final String POSTGRES_USERNAME = "postgres.username";
	public static final String POSTGRES_PASSWORD = "postgres.password";
	public static final String POSTGRES_URL = "postgres.url";

	// version materialisation properties
	public static final String USE_VERSION_MATERIALISATION = "use-version-materialisation";
	public static final String VERSION_OF_PROPERTY = "version-of-property";
	public static final String RESTRICT_TO_MEMBERS = "restrict-to-members";

}
