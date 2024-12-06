package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.InvalidConfigException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LdioLdesClientPropertiesTest {

	@Test
	void given_ExactlyOnceAndVersionMaterialisationAreBothExplicitlyEnabled_when_parseConfig_then_ThrowException() {
		final ComponentProperties properties = new ComponentProperties("pipeline", "cname", Map.of(
				LdioLdesClientPropertyKeys.USE_EXACTLY_ONCE_FILTER, String.valueOf(true),
				LdioLdesClientPropertyKeys.USE_VERSION_MATERIALISATION, String.valueOf(true)
		));

		assertThatThrownBy(() -> LdioLdesClientProperties.fromComponentProperties(properties))
				.isInstanceOf(InvalidConfigException.class)
				.hasMessage("Invalid config: \"The exactly once filter can not be enabled with version materialisation.\" .");
	}
}