package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LdioRepositoryMaterialiserPropertiesTest {
	public static final int BATCH_SIZE_DEFAULT = 10000;
	public static final int BATCH_TIMEOUT_DEFAULT = 120000;

	@Test
	void given_EmptyProperties_when_GetNamedGraph_then_ReturnEmptyString() {
		final LdioRepositoryMaterialiserProperties properties = new LdioRepositoryMaterialiserProperties(
				new ComponentProperties("pipelineName", "cName")
		);

		assertThat(properties.getNamedGraph()).isEmpty();
	}

	@Test
	void given_EmptyProperties_when_GetBatchProperties_then_ReturnDefaults() {
		final LdioRepositoryMaterialiserProperties properties = new LdioRepositoryMaterialiserProperties(
				new ComponentProperties("pipelineName", "cName")
		);

		assertThat(properties.getBatchSize()).isEqualTo(BATCH_SIZE_DEFAULT);
		assertThat(properties.getBatchTimeout()).isEqualTo(BATCH_TIMEOUT_DEFAULT);
	}

	@Test
	void given_ComponentProperties_when_GetProperties_Then_ReturnProperties() {
		final String sparqlHost = "localhost";
		final String repoId = "test-id";
		final String namedGraph = "http://named-graph";
		final int batchSize = 10;
		final int batchTimeout = 1200;

		final ComponentProperties config = new ComponentProperties(
				"pipelineName",
				"cName",
				Map.of(
						"sparql-host", sparqlHost,
						"repository-id", repoId,
						"namedGraph", namedGraph,
						"batchSize", String.valueOf(batchSize),
						"batchTimeout", String.valueOf(batchTimeout)
				));
		final LdioRepositoryMaterialiserProperties properties = new LdioRepositoryMaterialiserProperties(config);

		assertThat(properties.getSparqlHost()).isEqualTo(sparqlHost);
		assertThat(properties.getRepositoryId()).isEqualTo(repoId);
		assertThat(properties.getNamedGraph()).isEqualTo(namedGraph);
		assertThat(properties.getBatchSize()).isEqualTo(batchSize);
		assertThat(properties.getBatchTimeout()).isEqualTo(batchTimeout);
	}
}