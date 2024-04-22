package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.GeoJsonToWktTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
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

    public static class LdioGeoJsonToWktConfigurator implements LdioConfigurator {
        public static final String RDF_PLUS_WKT_ENABLED = "create-rdf-plus-wkt";

        @Override
        public LdiComponent configure(ComponentProperties config) {
            boolean rdfPlusWktEnabled = config.getOptionalBoolean(RDF_PLUS_WKT_ENABLED).orElse(false);
            return new GeoJsonToWktTransformer(rdfPlusWktEnabled);
        }
    }

}
