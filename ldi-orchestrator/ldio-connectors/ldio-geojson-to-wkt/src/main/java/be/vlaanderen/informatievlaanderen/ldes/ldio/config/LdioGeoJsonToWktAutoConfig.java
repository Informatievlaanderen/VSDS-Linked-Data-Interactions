package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioTransformerConfigurator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioGeoJsonToWktAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.GeoJsonToWktTransformer")
	public LdioTransformerConfigurator geoJsonToWktConfigurator() {
		return properties -> new LdioGeoJsonToWkt();
	}

}
