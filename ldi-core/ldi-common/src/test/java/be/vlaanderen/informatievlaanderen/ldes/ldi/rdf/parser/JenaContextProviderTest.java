package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.parser;

import org.junit.jupiter.api.Test;

import com.apicatalog.jsonld.JsonLdOptions;
import com.apicatalog.jsonld.context.cache.Cache;
import com.apicatalog.jsonld.document.Document;

import static org.apache.jena.riot.lang.LangJSONLD11.JSONLD_OPTIONS;
import static org.assertj.core.api.Assertions.assertThat;

class JenaContextProviderTest {

    @Test
    void withMaxJsonLdCacheCapacity_ShouldSetCacheCapacity() {
        final int capacity = 2;

        final var provider = JenaContextProvider.create().withMaxJsonLdCacheCapacity(capacity);

        JsonLdOptions jsonLdOptions = provider.getContext().get(JSONLD_OPTIONS);
        Cache<String, Document> documentCache = jsonLdOptions.getDocumentCache();

        documentCache.put("keyA", null);
        documentCache.put("keyB", null);
        documentCache.put("keyC", null);

        // An LRU cache with a capacity of 2 should only contain the last two entries
        assertThat(documentCache.containsKey("keyA")).isFalse();
        assertThat(documentCache.containsKey("keyB")).isTrue();
        assertThat(documentCache.containsKey("keyC")).isTrue();
    }

    @Test
    void withCreate_ShouldContainDefaults() {
        final var provider = JenaContextProvider.create();
        JsonLdOptions jsonLdOptions = provider.getContext().get(JSONLD_OPTIONS);

        assertThat(jsonLdOptions).isNotNull();
        assertThat(jsonLdOptions.getDocumentCache()).isNotNull();
    }
}
