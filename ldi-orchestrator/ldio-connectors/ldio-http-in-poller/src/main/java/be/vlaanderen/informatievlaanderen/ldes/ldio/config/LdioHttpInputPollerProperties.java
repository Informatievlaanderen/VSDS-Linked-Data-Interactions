package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

public class LdioHttpInputPollerProperties {

	private LdioHttpInputPollerProperties() {
	}

	public static final String URL = "url";
	public static final String INTERVAL = "interval";
	public static final String CRON = "cron";
	public static final String CONTINUE_ON_FAIL = "continueOnFail";

	public static final String INVALID_PROPERTY = "Invalid config for the ldio http in poller: %s cannot have following value: %s";
	public static final String INTERVAL_MIGRATION_WARNING = "'interval' property is deprecated. Please consider migrating to 'cron' property";

}
