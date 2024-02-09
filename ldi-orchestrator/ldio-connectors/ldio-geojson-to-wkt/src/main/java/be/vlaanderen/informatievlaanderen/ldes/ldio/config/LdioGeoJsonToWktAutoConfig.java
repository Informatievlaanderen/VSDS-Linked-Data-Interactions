package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioGeoJsonToWkt;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioTransformerConfigurator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioGeoJsonToWkt.NAME;

@Configuration
public class LdioGeoJsonToWktAutoConfig {

	@Bean(NAME)
	public LdioTransformerConfigurator geoJsonToWktConfigurator() {
		return properties -> new LdioGeoJsonToWkt();
	}

}
