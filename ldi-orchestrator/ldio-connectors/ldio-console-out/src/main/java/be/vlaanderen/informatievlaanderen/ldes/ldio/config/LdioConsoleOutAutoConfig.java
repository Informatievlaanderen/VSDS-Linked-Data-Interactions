package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdiConsoleOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioOutputConfigurator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties.RDF_WRITER;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdiConsoleOut.NAME;

@Configuration
public class LdioConsoleOutAutoConfig {
	@SuppressWarnings("java:S6830")
	@Bean(NAME)
	public LdioOutputConfigurator ldioConfigurator() {
		return new LdioConsoleOutConfigurator();
	}

	public static class LdioConsoleOutConfigurator implements LdioOutputConfigurator {

		@Override
		public LdiComponent configure(ComponentProperties config) {
			LdiRdfWriterProperties writerProperties = new LdiRdfWriterProperties(
					config.extractNestedProperties(RDF_WRITER).getConfig());

			return new LdiConsoleOut(writerProperties);
		}
	}
}
