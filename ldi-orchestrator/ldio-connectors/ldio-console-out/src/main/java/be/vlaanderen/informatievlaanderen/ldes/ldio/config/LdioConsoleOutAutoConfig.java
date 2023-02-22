package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdiConsoleOut;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import org.apache.jena.riot.Lang;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdiConsoleOut.getLang;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.ldio")
public class LdioConsoleOutAutoConfig {
	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioConsoleOut")
	public LdioConfigurator ldioConfigurator() {
		return new LdioConsoleOutConfigurator();
	}

	public static class LdioConsoleOutConfigurator implements LdioConfigurator {
		private final Lang DEFAULT_OUTPUT_LANG = Lang.NQUADS;

		@Override
		public LdiComponent configure(ComponentProperties properties) {
			Lang outputLanguage = properties.getOptionalProperty("content-type")
					.map(contentType -> getLang(MediaType.valueOf(contentType)))
					.orElse(DEFAULT_OUTPUT_LANG);
			return new LdiConsoleOut(outputLanguage);
		}
	}
}