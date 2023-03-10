package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.SparqlConstructTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class LdioSparqlConstructAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.SparqlConstructTransformer")
	public LdioConfigurator ldiHttpOutConfigurator() {
		return new LdioSparqlConstructConfigurator();
	}

	public static class LdioSparqlConstructConfigurator implements LdioConfigurator {
		public static final String QUERY = "query";
		public static final String INFER = "infer";

		@Override
		public LdiComponent configure(ComponentProperties config) {
			Query query = QueryFactory.create(config.getProperty(QUERY));
			boolean inferMode = config.getOptionalProperty(INFER).map(Boolean::parseBoolean).orElse(false);
			return new SparqlConstructTransformer(query, inferMode);
		}
	}

}
