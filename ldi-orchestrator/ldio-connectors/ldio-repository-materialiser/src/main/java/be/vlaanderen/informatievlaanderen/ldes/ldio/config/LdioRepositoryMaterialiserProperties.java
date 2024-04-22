package be.vlaanderen.informatievlaanderen.ldes.ldio.config;


public class LdioRepositoryMaterialiserProperties {
	public static final String SPARQL_HOST = "sparql-host";
	public static final String REPOSITORY_ID = "repository-id";
	public static final String NAMED_GRAPH = "named-graph";
	public static final String BATCH_SIZE = "batch-size";
	public static final String BATCH_TIMEOUT = "batch-timeout";

	public static final int BATCH_SIZE_DEFAULT = 500;
	public static final int BATCH_TIMEOUT_DEFAULT = 120000;

	private LdioRepositoryMaterialiserProperties() {
	}
}
