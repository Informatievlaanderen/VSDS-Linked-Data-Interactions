package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioSparqlConstruct;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.LdioTransformerConfigurator;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioSparqlConstruct.NAME;

@Configuration
public class LdioSparqlConstructAutoConfig {

	@Bean(NAME)
	public LdioTransformerConfigurator ldioConfigurator() {
		return new LdioSparqlConstructTransformerConfigurator();
	}

	public static class LdioSparqlConstructTransformerConfigurator implements LdioTransformerConfigurator {
		public static final String QUERY = "query";
		public static final String INFER = "infer";

		@Override
		public LdioTransformer configure(ComponentProperties config) {
			String queryContents = config.getOptionalPropertyFromFile(QUERY).orElse(config.getProperty(QUERY));
			Query query = QueryFactory.create(queryContents);
			boolean inferMode = config.getOptionalBoolean(INFER).orElse(false);
			return new LdioSparqlConstruct(query, inferMode);
		}
	}

}
