package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioFileOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.TimestampExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.jena.rdf.model.Property;
import org.springframework.context.annotation.Bean;

import java.io.File;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class LdioFileOutAutoConfig {

	public static final String ARCHIVE_ROOT_DIR_PROP = "archive-root-dir";
	public static final String TIMESTAMP_PATH_PROP = "timestamp-path";

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioFileOut")
	public LdioConfigurator ldiFileOutConfigurator() {
		return new LdioConfigurator() {
			@Override
			public LdiComponent configure(ComponentProperties properties) {
				String archiveDirectory = properties.getProperty(ARCHIVE_ROOT_DIR_PROP);
				Property timestampPath = createProperty(properties.getProperty(TIMESTAMP_PATH_PROP));
				return new LdioFileOut(new TimestampExtractor(timestampPath), removeTrailingSlash(archiveDirectory));
			}

			private String removeTrailingSlash(String path) {
				int indexLastChar = path.length() - 1;
				String substring = path.substring(indexLastChar);
				if (File.separator.equals(substring)) {
					return path.substring(0, indexLastChar);
				} else {
					return path;
				}
			}
		};
	}

}
