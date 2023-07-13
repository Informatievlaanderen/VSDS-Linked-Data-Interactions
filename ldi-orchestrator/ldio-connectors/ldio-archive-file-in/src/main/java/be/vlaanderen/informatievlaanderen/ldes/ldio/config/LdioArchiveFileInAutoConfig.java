package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.ArchiveFileCrawler;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioArchiveFileIn;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioArchiveFileInRunner;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
					ComponentProperties config) {
				ArchiveFileCrawler archiveFileCrawler = new ArchiveFileCrawler(getArchiveDirectoryPath(config));
				Lang hintLang = getSourceFormat(config);
				var ldioArchiveFileInRunner = new LdioArchiveFileInRunner(executor, archiveFileCrawler, hintLang);
				return new LdioArchiveFileIn(executor, ldioArchiveFileInRunner);
			}

			private Path getArchiveDirectoryPath(ComponentProperties config) {
				String directory = config.getProperty(ARCHIVE_ROOT_DIR_PROP);
				return Paths.get(directory);
			}

			private Lang getSourceFormat(ComponentProperties config) {
				return config.getOptionalProperty(SOURCE_FORMAT)
						.map(RDFLanguages::nameToLang)
						.orElse(null);
			}

		};
	}

}
