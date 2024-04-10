package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.config;

import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LdesDiscovererConfigTest {

	private LdesDiscovererConfig config;

	@BeforeEach
	void setUp() {
		config = new LdesDiscovererConfig();
	}

	@Test
	void given_EmptyConfig_when_GetSourceFormatAsLang_then_ReturnNQuads() {
		final Lang defaultSourceLang = Lang.TURTLE;

		final Lang actualSourceLang = config.getSourceFormatAsLang();

		assertThat(actualSourceLang).isEqualTo(defaultSourceLang);
	}

	@Test
	void given_EmptyConfig_when_GetOutputFormatAsLang_then_ReturnTurtle() {
		final Lang defaultOutputLang = Lang.TURTLE;

		final Lang actualOutputLang = config.getOutputFormatAsLang();

		assertThat(actualOutputLang).isEqualTo(defaultOutputLang);
	}

	@Test
	void given_EmptyConfig_when_GetUrl_then_ReturnUrl() {
		assertThatThrownBy(() -> config.getUrl())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Missing value for 'url'");
	}

	@Test
	void given_ConfigWithJsonLdSourceFormat_when_GetSourceFormatAsLang_then_ReturnJsonLd() {
		config.setSourceFormat("application/ld+json");
		final Lang expectedSourceLang = Lang.JSONLD;

		final Lang actualSourceLang = config.getSourceFormatAsLang();

		assertThat(actualSourceLang).isEqualTo(expectedSourceLang);
	}

	@Test
	void testSetOutputFormat() {
		config.setOutputFormat("application/ld+json");
		final Lang expectedOutputLang = Lang.JSONLD;

		final Lang actualSourceLang = config.getOutputFormatAsLang();

		assertThat(actualSourceLang).isEqualTo(expectedOutputLang);
	}
}
