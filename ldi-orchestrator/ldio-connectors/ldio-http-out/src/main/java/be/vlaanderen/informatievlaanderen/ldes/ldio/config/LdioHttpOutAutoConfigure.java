package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

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

import java.util.Map;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpOut.getLang;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class LdioHttpOutAutoConfigure {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpOut")
	public LdioHttpOutConfigurator ldiHttpOutConfigurator() {
		return new LdioHttpOutConfigurator();
	}

	public static class LdioHttpOutConfigurator implements LdioConfigurator {
		@Override
		public LdiComponent configure(Map<String, String> config) {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();

			Lang outputLanguage;

			if (config.containsKey(CONTENT_TYPE)) {
				outputLanguage = getLang(
						Objects.requireNonNull(MediaType.valueOf(config.get(CONTENT_TYPE))));

				headers.setContentType(MediaType.valueOf(outputLanguage.getContentType().getContentTypeStr()));
			}
			else {
				outputLanguage = Lang.NQUADS;
			}

			String targetURL = Objects.requireNonNull(config.get("endpoint"));

			return new LdioHttpOut(restTemplate, headers, outputLanguage, targetURL);
		}
	}
}
