package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.ArchiveFileCrawler;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioArchiveFileIn;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioInputConfigurator;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class LdioArchiveFileInAutoConfig {
	public static final String ARCHIVE_ROOT_DIR_PROP = "archive-root-dir";
	public static final String SOURCE_FORMAT = "source-format";

	@SuppressWarnings("java:S6830")
	@Bean(LdioArchiveFileIn.NAME)
	public LdioInputConfigurator ldiArchiveFileInConfigurator(ObservationRegistry observationRegistry) {
		return new LdioInputConfigurator() {
			@Override
			public LdioInput configure(LdiAdapter adapter,
									   ComponentExecutor executor,
									   ApplicationEventPublisher applicationEventPublisher,
									   ComponentProperties config) {
				ArchiveFileCrawler archiveFileCrawler = new ArchiveFileCrawler(getArchiveDirectoryPath(config));
				Lang hintLang = getSourceFormat(config);
				String pipelineName = config.getPipelineName();

				return new LdioArchiveFileIn(pipelineName, executor, observationRegistry, applicationEventPublisher, archiveFileCrawler, hintLang);
			}

			@Override
			public boolean isAdapterRequired() {
				return false;
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
