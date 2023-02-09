package be.vlaanderen.informatievlaanderen.ldes.ldto.transformer;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.transformer")
public class SqparqlConstructTransformerWrapper {

    @Bean("sparql")
    public SparqlConstructorConfigurator sparqlConstructorConfigurator() {
        return new SparqlConstructorConfigurator();
    }
}
