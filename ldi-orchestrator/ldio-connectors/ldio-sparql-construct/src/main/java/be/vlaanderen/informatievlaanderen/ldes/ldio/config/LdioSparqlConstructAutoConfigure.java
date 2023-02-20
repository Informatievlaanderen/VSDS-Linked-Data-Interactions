package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.SparqlConstructTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Objects;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class LdioSparqlConstructAutoConfigure {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.SparqlConstructTransformer")
	public LdioConfigurator ldiHttpOutConfigurator() {
		return new LdioSparqlConstructConfigurator();
	}

	public static class LdioSparqlConstructConfigurator implements LdioConfigurator {
		public static final String QUERY = "query";
		public static final String QUERY_VALIDATION_MSG = "Must provide a valid construct query";
		public static final String INFER = "infer";

		@Override
		public LdiComponent configure(Map<String, String> config) {
			Query query = QueryFactory.create(Objects.requireNonNull(config.get(QUERY), QUERY_VALIDATION_MSG));
			boolean inferMode = Boolean.parseBoolean(config.getOrDefault(INFER, String.valueOf(false)));
			return new SparqlConstructTransformer(query, inferMode);
		}
	}

}
