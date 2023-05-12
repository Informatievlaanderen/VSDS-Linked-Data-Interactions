package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdiConsoleOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.jena.riot.Lang;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdiConsoleOut.getLang;

@Configuration
public class LdioConsoleOutAutoConfig {
	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioConsoleOut")
	public LdioConfigurator ldioConfigurator() {
		return new LdioConsoleOutConfigurator();
	}

	public static class LdioConsoleOutConfigurator implements LdioConfigurator {
		private static final Lang DEFAULT_OUTPUT_LANG = Lang.NQUADS;

		@Override
		public LdiComponent configure(ComponentProperties properties) {
			Lang outputLanguage = properties.getOptionalProperty("content-type")
					.map(contentType -> getLang(MediaType.valueOf(contentType)))
					.orElse(DEFAULT_OUTPUT_LANG);
			return new LdiConsoleOut(outputLanguage);
		}
	}
}
