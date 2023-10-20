package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdiConsoleOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties.RDF_WRITER;

@Configuration
public class LdioConsoleOutAutoConfig {
    @Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioConsoleOut")
    public LdioConfigurator ldioConfigurator() {
        return new LdioConsoleOutConfigurator();
    }

    public static class LdioConsoleOutConfigurator implements LdioConfigurator {

        @Override
        public LdiComponent configure(ComponentProperties config) {
            LdiRdfWriterProperties writerProperties
                    = new LdiRdfWriterProperties(config.extractNestedProperties(RDF_WRITER).getConfig());

            return new LdiConsoleOut(writerProperties);
        }
    }
}
