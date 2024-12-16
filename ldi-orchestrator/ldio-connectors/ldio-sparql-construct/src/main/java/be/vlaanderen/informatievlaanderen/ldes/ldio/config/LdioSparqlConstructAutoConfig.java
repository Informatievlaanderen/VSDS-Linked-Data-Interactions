package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.datasetsplitter.DatasetSplitter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.datasetsplitter.DatasetSplitters;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioSparqlConstruct;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioTransformerConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioSparqlConstruct.NAME;

@Configuration
public class LdioSparqlConstructAutoConfig {

	@SuppressWarnings("java:S6830")
	@Bean(NAME)
	public LdioTransformerConfigurator ldioConfigurator() {
		return new LdioSparqlConstructTransformerConfigurator();
	}

	public static class LdioSparqlConstructTransformerConfigurator implements LdioTransformerConfigurator {
		public static final String QUERY = "query";
		public static final String INFER = "infer";
		public static final String SPLIT_BY_NAMED_GRAPH = "splitByNamedGraph";

		@Override
		public LdioTransformer configure(ComponentProperties config) {
			String queryContents = config.getOptionalPropertyFromFile(QUERY).orElse(config.getProperty(QUERY));
			Query query = QueryFactory.create(queryContents);
			boolean inferMode = config.getOptionalBoolean(INFER).orElse(false);
			boolean splitByNamedGraph = config.getOptionalBoolean(SPLIT_BY_NAMED_GRAPH).orElse(true);
			DatasetSplitter datasetSplitter = splitByNamedGraph
					? DatasetSplitters.splitByNamedGraph()
					: DatasetSplitters.preventSplitting();

			return new LdioSparqlConstruct(query, inferMode, datasetSplitter);
		}
	}

}
