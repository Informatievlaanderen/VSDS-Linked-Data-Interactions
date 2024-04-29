package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioGeoJsonToWkt;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioTransformerConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioGeoJsonToWkt.NAME;

@Configuration
public class LdioGeoJsonToWktAutoConfig {

	@Bean(NAME)
	public LdioTransformerConfigurator geoJsonToWktConfigurator() {
        return new LdioGeoJsonToWktConfigurator();
	}

    public static class LdioGeoJsonToWktConfigurator implements LdioTransformerConfigurator {
        public static final String TRANSFORM_TO_RDF_WKT = "transform-to-rdf+wkt-enabled";

        @Override
        public LdioTransformer configure(ComponentProperties config) {
            boolean transformToRdfWkt = config.getOptionalBoolean(TRANSFORM_TO_RDF_WKT).orElse(false);
            return new LdioGeoJsonToWkt(transformToRdfWkt);
        }
    }

}
