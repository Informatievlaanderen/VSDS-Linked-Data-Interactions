package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.ArchiveFileReader;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioArchiveFileIn;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class LdioArchiveFileInAutoConfig {

	public static final String ARCHIVE_ROOT_DIR_PROP = "archive-root-dir";
	public static final String SOURCE_FORMAT = "source-format";

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioArchiveFileIn")
	public LdioInputConfigurator ldiArchiveFileInConfigurator() {
		return new LdioInputConfigurator() {
			@Override
			public LdiComponent configure(LdiAdapter adapter,
										  ComponentExecutor executor,
										  ComponentProperties config){
				ArchiveFileReader archiveFileReader = new ArchiveFileReader(getArchiveDirectoryPath(config));
				Lang sourceFormat = getSourceFormat(config);
				return new LdioArchiveFileIn(adapter, executor, archiveFileReader, sourceFormat);
			}

			private Path getArchiveDirectoryPath(ComponentProperties config) {
				String directory = config.getProperty(ARCHIVE_ROOT_DIR_PROP);
				return Paths.get(removeTrailingSlash(directory));
			}

			private Lang getSourceFormat(ComponentProperties config) {
				return config.getOptionalProperty(SOURCE_FORMAT)
						.map(RDFLanguages::nameToLang)
						.orElse(null);
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
