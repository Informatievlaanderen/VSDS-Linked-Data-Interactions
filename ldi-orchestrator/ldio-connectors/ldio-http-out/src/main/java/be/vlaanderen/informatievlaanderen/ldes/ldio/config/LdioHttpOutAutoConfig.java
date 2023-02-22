package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpOut;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import org.apache.jena.riot.Lang;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpOut.getLang;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class LdioHttpOutAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpOut")
	public LdioHttpOutConfigurator ldiHttpOutConfigurator() {
		return new LdioHttpOutConfigurator();
	}

	public static class LdioHttpOutConfigurator implements LdioConfigurator {
		private final Lang DEFAULT_OUTPUT_LANG = Lang.NQUADS;

		@Override
		public LdiComponent configure(ComponentProperties config) {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();

			Lang outputLanguage = config.getOptionalProperty("content-type")
					.map(contentType -> {
						Lang lang = getLang(Objects.requireNonNull(MediaType.valueOf(contentType)));
						headers.setContentType(MediaType.valueOf(lang.getContentType().getContentTypeStr()));
						return lang;
					})
					.orElse(DEFAULT_OUTPUT_LANG);

			String targetURL = config.getProperty("endpoint");

			return new LdioHttpOut(restTemplate, headers, outputLanguage, targetURL);
		}
	}
}
