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
                // TODO: 07/07/23 get path from properties
                String basePath = "/home/tom/IdeaProjects/VSDS-Linked-Data-Interactions/ldi-orchestrator/ldio-connectors/ldio-file-out/archive";
                Property timestampPath = createProperty("");

                return new LdioFileOut(new TimestampExtractor(timestampPath), removeTrailingSlash(basePath));
            }

            private String removeTrailingSlash(String path) {
                return path.replaceAll("/$", "");
            }
        };
    }


}
