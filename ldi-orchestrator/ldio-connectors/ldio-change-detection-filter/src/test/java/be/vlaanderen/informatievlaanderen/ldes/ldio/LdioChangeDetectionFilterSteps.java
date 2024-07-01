package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioChangeDetectionFilterAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioVaultTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.LdioTransformerConfigurator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.junit.After;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioTransformer.link;
import static org.assertj.core.api.Assertions.assertThat;

public class LdioChangeDetectionFilterSteps {
	private static final String PIPELINE_NAME = "my-pipeline";
	private LdioChangeDetectionFilter ldioChangeDetectionFilter;
	private LdioVaultTransformer vault;
	private PostgreSQLContainer<?> postgreSQLContainer;

	@After
	public void teardown() {
		if (postgreSQLContainer != null) {
			postgreSQLContainer.stop();
			postgreSQLContainer = null;
		}
	}

	private Map<String, String> startPostgresContainer() {
		postgreSQLContainer = new PostgreSQLContainer<>("postgres:11.1")
				.withDatabaseName("integration-change-detection-filter-persistence")
				.withUsername("sa")
				.withPassword("sa");
		postgreSQLContainer.start();

		return Map.of(
				"postgres.url", postgreSQLContainer.getJdbcUrl(),
				"postgres.username", postgreSQLContainer.getUsername(),
				"postgres.password", postgreSQLContainer.getPassword()
		);
	}

	@When("I send model from {string} to the LdioChangeDetectionFilter")
	public void iSendModelFromToTheLdioChangeDetectionFilter(String fileName) {
		final Model model = RDFParser.source(fileName).toModel();
		ldioChangeDetectionFilter.apply(model);
	}

	@Then("The result contains {int} model")
	public void theResultContainsModel(int numberOfModels) {
		assertThat(vault.getModels()).hasSize(numberOfModels);
	}

	@Given("An LdioChangeDetectionFilter with a {string} persistence strategy")
	public void anLdioChangeDetectionFilterWithAStatePersistenceStrategy(String persistenceStrategy) {
		final Map<String, String> properties = new HashMap<>(Map.of("keep-state", "false", "state", persistenceStrategy));
		if(persistenceStrategy.equals("POSTGRES")) {
			final var postgresProperties = startPostgresContainer();
			properties.putAll(postgresProperties);
		}
		vault = new LdioVaultTransformer();
		final LdioTransformerConfigurator configurator = new LdioChangeDetectionFilterAutoConfig.LdioChangeDetectionFilterConfigurator();
		final ComponentProperties componentProperties = new ComponentProperties(PIPELINE_NAME, "Ldio:ChangeDetectionFitler", properties);
		ldioChangeDetectionFilter = (LdioChangeDetectionFilter) link(configurator.configure(componentProperties), List.of(vault));
	}

	@Then("I shutdown the LdioChangeDetectionFilter")
	public void iShutdownTheLdioChangeDetectionFilter() {
		ldioChangeDetectionFilter.shutdown();
	}
}
