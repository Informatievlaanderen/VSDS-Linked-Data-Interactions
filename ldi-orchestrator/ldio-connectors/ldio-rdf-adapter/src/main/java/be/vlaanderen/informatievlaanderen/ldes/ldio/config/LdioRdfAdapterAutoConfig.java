package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.RdfAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioAdapterConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import com.apicatalog.jsonld.JsonLdOptions;
import com.apicatalog.jsonld.context.cache.LruCache;
import org.apache.jena.riot.RIOT;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.ContextAccumulator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.apache.jena.riot.lang.LangJSONLD11.JSONLD_OPTIONS;

@Configuration
public class LdioRdfAdapterAutoConfig {

	public static final String MAX_JSONLD_CACHE_CAPACITY = "max-jsonld-cache-capacity";

	@SuppressWarnings("java:S6830")
	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.RdfAdapter")
	public LdioAdapterConfigurator ldiHttpOutConfigurator() {
		return new LdioRdfConfigurator();
	}

	public static class LdioRdfConfigurator implements LdioAdapterConfigurator {

		@Override
		public LdiAdapter configure(ComponentProperties config) {
			final int maxCacheCapacity = config.getOptionalInteger(MAX_JSONLD_CACHE_CAPACITY).orElse(100);
			return new RdfAdapter(prepareContext(maxCacheCapacity));
		}

		private Context prepareContext(final int maxCacheCapacity) {
			final var options = new JsonLdOptions();
			options.setDocumentCache(new LruCache<>(maxCacheCapacity));
            return ContextAccumulator.newBuilder(RIOT::getContext).context().set(JSONLD_OPTIONS, options);
		}

	}
}
