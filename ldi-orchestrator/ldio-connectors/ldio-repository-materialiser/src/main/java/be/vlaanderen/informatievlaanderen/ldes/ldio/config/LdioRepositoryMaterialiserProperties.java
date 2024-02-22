package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;

public class LdioRepositoryMaterialiserProperties {
	public static final String SPARQL_HOST = "sparql-host";
	public static final String REPOSITORY_ID = "repository-id";
	public static final String NAMED_GRAPH = "named-graph";
	public static final String BATCH_SIZE = "batch-size";
	public static final String BATCH_TIMEOUT = "batch-timeout";

	public static final int BATCH_SIZE_DEFAULT = 10000;
	public static final int BATCH_TIMEOUT_DEFAULT = 120000;

	private final ComponentProperties componentProperties;

	public LdioRepositoryMaterialiserProperties(ComponentProperties componentProperties) {
		this.componentProperties = componentProperties;
	}

	public String getSparqlHost() {
		return componentProperties.getProperty(SPARQL_HOST);
	}

	public String getRepositoryId() {
		return componentProperties.getProperty(REPOSITORY_ID);
	}

	public String getNamedGraph() {
		return componentProperties.getOptionalProperty(NAMED_GRAPH).orElse("");
	}

	public int getBatchSize() {
		return componentProperties.getOptionalInteger(BATCH_SIZE).orElse(BATCH_SIZE_DEFAULT);
	}

	public int getBatchTimeout() {
		return componentProperties.getOptionalInteger(BATCH_TIMEOUT).orElse(BATCH_TIMEOUT_DEFAULT);
	}

}
