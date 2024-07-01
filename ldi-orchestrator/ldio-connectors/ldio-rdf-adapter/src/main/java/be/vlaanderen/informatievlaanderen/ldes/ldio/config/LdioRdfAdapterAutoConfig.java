package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.RdfAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.parser.JenaContextProvider;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.LdioAdapterConfigurator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioRdfAdapterAutoConfig {

	public static final String MAX_JSONLD_CACHE_CAPACITY = "max-jsonld-cache-capacity";

	@SuppressWarnings("java:S6830")
	@Bean("Ldio:RdfAdapter")
	public LdioAdapterConfigurator ldioAdapterConfigurator() {
		return new LdioRdfConfigurator();
	}

	public static class LdioRdfConfigurator implements LdioAdapterConfigurator {

		@Override
		public LdiAdapter configure(ComponentProperties config) {
			final int maxCacheCapacity = config.getOptionalInteger(MAX_JSONLD_CACHE_CAPACITY).orElse(100);
			final var context = JenaContextProvider.create().withMaxJsonLdCacheCapacity(maxCacheCapacity).getContext();
			return new RdfAdapter(context);
		}

	}
}
