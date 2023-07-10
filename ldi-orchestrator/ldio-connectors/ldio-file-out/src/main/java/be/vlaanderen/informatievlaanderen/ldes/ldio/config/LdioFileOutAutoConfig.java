package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioFileOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.TimestampExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.jena.rdf.model.Property;
import org.springframework.context.annotation.Bean;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class LdioFileOutAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpOut")
	public LdioConfigurator ldiHttpOutConfigurator() {
		return new LdioConfigurator() {
			@Override
			public LdiComponent configure(ComponentProperties properties) {
				String archiveDirectory = properties.getProperty("archive-root-dir");
				Property timestampPath = createProperty(properties.getProperty("timestamp-path"));
				return new LdioFileOut(new TimestampExtractor(timestampPath), removeTrailingSlash(archiveDirectory));
			}

			private String removeTrailingSlash(String path) {
				return path.replaceAll("/$", "");
			}
		};
	}

}
