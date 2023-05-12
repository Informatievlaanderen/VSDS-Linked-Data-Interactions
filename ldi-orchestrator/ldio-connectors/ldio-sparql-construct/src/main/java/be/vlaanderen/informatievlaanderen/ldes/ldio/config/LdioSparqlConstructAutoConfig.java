package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.SparqlConstructTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
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
	public LdioConfigurator ldioConfigurator() {
		return new LdioSparqlConstructConfigurator();
	}

	public static class LdioSparqlConstructConfigurator implements LdioConfigurator {
		public static final String QUERY = "query";
		public static final String INFER = "infer";

		@Override
		public LdiComponent configure(ComponentProperties config) {
			String queryContents = config.getOptionalPropertyFromFile(QUERY).orElse(config.getProperty(QUERY));
			Query query = QueryFactory.create(queryContents);
			boolean inferMode = config.getOptionalBoolean(INFER).orElse(false);
			return new SparqlConstructTransformer(query, inferMode);
		}
	}

}
