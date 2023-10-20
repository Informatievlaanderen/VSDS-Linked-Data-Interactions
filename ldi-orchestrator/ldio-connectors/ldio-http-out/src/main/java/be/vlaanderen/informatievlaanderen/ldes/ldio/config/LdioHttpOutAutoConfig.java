package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.jena.riot.Lang;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties.RDF_WRITER;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpOut.getLang;

@Configuration
public class LdioHttpOutAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpOut")
	public LdioHttpOutConfigurator ldiHttpOutConfigurator() {
		return new LdioHttpOutConfigurator();
	}

	public static class LdioHttpOutConfigurator implements LdioConfigurator {

		@Override
		public LdiComponent configure(ComponentProperties config) {
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			HttpHeaders headers = new HttpHeaders();

			String targetURL = config.getProperty("endpoint");

			return new LdioHttpOut(restTemplate, headers, targetURL,
					new LdiRdfWriterProperties(config.extractNestedProperties(RDF_WRITER).getConfig()));
		}
	}
}
