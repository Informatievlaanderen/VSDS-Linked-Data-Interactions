package be.vlaanderen.informatievlaanderen.ldes.ldio.config.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioSkolemisationTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioTransformerConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.ConfigPropertyMissingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LdioSkolemisationTransformerAutoConfigTest {
	private LdioTransformerConfigurator ldioSkolemisationTransformerConfigurator;

	@BeforeEach
	void setUp() {
		ldioSkolemisationTransformerConfigurator = new LdioSkolemisationTransformerAutoConfig().ldioConfigurator();
	}

	@Test
	void test_MissingProperty() {
		final ComponentProperties properties = new ComponentProperties("pipeline", "component");

		assertThatThrownBy(() -> ldioSkolemisationTransformerConfigurator.configure(properties))
				.isInstanceOf(ConfigPropertyMissingException.class)
				.hasMessage("Pipeline \"pipeline\": \"component\" : Missing value for property \"skolem-domain\" .");
	}

	@Test
	void test_Configure() {
		final ComponentProperties properties = new ComponentProperties("pipeline", "component", Map.of("skolem-domain", "http://exampe.com"));

		final LdioTransformer result = ldioSkolemisationTransformerConfigurator.configure(properties);

		assertThat(result).isInstanceOf(LdioSkolemisationTransformer.class);
	}

}