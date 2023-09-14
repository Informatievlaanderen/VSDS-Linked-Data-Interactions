package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioProcessorConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioProcessor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioSparqlConstructAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.SparqlConstructTransformer")
	public LdioProcessorConfigurator ldioConfigurator() {
		return new LdioSparqlConstructProcessorConfigurator();
	}

	public static class LdioSparqlConstructProcessorConfigurator implements LdioProcessorConfigurator {
		public static final String QUERY = "query";
		public static final String INFER = "infer";

		@Override
		public LdioProcessor configure(ComponentProperties config) {
			String queryContents = config.getOptionalPropertyFromFile(QUERY).orElse(config.getProperty(QUERY));
			Query query = QueryFactory.create(queryContents);
			boolean inferMode = config.getOptionalBoolean(INFER).orElse(false);
			return new LdioSparqlConstruct(query, inferMode);
		}
	}

}
