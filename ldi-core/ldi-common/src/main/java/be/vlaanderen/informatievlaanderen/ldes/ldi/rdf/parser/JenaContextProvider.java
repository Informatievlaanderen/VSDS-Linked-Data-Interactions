package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.parser;

import com.apicatalog.jsonld.JsonLdOptions;
import com.apicatalog.jsonld.context.cache.LruCache;
import org.apache.jena.riot.RIOT;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.ContextAccumulator;

import static org.apache.jena.riot.lang.LangJSONLD11.JSONLD_OPTIONS;

public class JenaContextProvider {

    private int maxJsonLdCacheCapacity = 100;

    public static JenaContextProvider create() {
        return new JenaContextProvider();
    }

    public JenaContextProvider withMaxJsonLdCacheCapacity(int maxJsonLdCacheCapacity) {
        this.maxJsonLdCacheCapacity = maxJsonLdCacheCapacity;
        return this;
    }

    public Context getContext() {
        final var options = new JsonLdOptions();
        options.setDocumentCache(new LruCache<>(maxJsonLdCacheCapacity));
        return ContextAccumulator.newBuilder(RIOT::getContext).context().set(JSONLD_OPTIONS, options);
    }

}
