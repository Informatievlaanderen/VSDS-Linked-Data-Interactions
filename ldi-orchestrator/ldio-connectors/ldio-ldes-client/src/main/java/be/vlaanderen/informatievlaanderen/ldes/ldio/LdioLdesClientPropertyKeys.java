package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.apache.jena.riot.Lang;

public class LdioLdesClientPropertyKeys {
	private LdioLdesClientPropertyKeys() {
	}

	// general properties
	public static final String URLS = "urls";
	public static final String SOURCE_FORMAT = "source-format";
	public static final Lang DEFAULT_SOURCE_FORMAT = Lang.TURTLE;

	public static final String USE_EXACTLY_ONCE_FILTER = "enable-exactly-once";

	// version materialisation properties
	public static final String USE_VERSION_MATERIALISATION = "materialisation.enabled";
	public static final String USE_LATEST_STATE_FILTER = "materialisation.enable-latest-state";
}
