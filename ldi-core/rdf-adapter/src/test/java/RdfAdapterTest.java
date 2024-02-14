import be.vlaanderen.informatievlaanderen.ldes.ldi.RdfAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import com.apicatalog.jsonld.JsonLdOptions;
import com.apicatalog.jsonld.context.cache.LruCache;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RIOT;
import org.apache.jena.sparql.util.ContextAccumulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.apache.jena.riot.lang.LangJSONLD11.JSONLD_OPTIONS;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WireMockTest(httpPort = 10101)
class RdfAdapterTest {

	private static final String expectedQuads = """
			_:b0 <http://schema.org/jobTitle> "Professor" .
			_:b0 <http://schema.org/name> "Jane Doe" .
			_:b0 <http://schema.org/telephone> "(425) 123-4567" .
			_:b0 <http://schema.org/url> <http://www.janedoe.com> .
			_:b0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://schema.org/Person> .
			""";

	private static final Model expectedModel = RDFParserBuilder.create().fromString(expectedQuads).lang(Lang.NQUADS)
			.toModel();

	private RdfAdapter adapter;

	@BeforeEach
	void setUp() {
		final var options = new JsonLdOptions();
		options.setDocumentCache(new LruCache<>(5));
		final var context = ContextAccumulator.newBuilder(RIOT::getContext).context().set(JSONLD_OPTIONS, options);
		adapter = new RdfAdapter(context);
	}

	@Test
	void adapt_jsonLd() {
		String content = """
				{
				  "@context": "http://schema.org/",
				  "@type": "Person",
				  "name": "Jane Doe",
				  "jobTitle": "Professor",
				  "telephone": "(425) 123-4567",
				  "url": "http://www.janedoe.com"
				}""";

		Model result = adapter.apply(LdiAdapter.Content.of(content, "application/ld+json"))
				.findFirst()
				.orElseThrow();

		assertTrue(result.isIsomorphicWith(expectedModel));
	}

	@Test
	void adapt_turtle() {
		String content = """
				@prefix schema: <http://schema.org/> .

				 []
				   schema:jobTitle "Professor" ;
				   schema:name "Jane Doe" ;
				   schema:telephone "(425) 123-4567" ;
				   schema:url <http://www.janedoe.com> ;
				   a schema:Person .
				""";

		Model result = adapter.apply(LdiAdapter.Content.of(content, "text/turtle"))
				.findFirst()
				.orElseThrow();

		assertTrue(result.isIsomorphicWith(expectedModel));
	}

	@Test
	void when_ContextIsUsedForParsingMultipleModels_JsonLdContextIsOnlyFetchedOnceFromExternalSource() {
		String content = """
				{
				  "@context": [
				    "http://localhost:10101/context.jsonld"
				  ],
				  "@type": "Article",
				  "headline": "The Future of Technology",
				  "author": {
				    "@type": "Person",
				    "name": "Jane Doe"
				  },
				  "datePublished": "2024-01-31",
				  "publisher": {
				    "@type": "Organization",
				    "name": "Tech News",
				    "logo": {
				      "@type": "ImageObject",
				      "url": "https://example.com/logo.png"
				    }
				  },
				  "image": {
				    "@type": "ImageObject",
				    "url": "https://example.com/article-image.jpg",
				    "height": 800,
				    "width": 1200
				  },
				  "articleBody": "This is an in-depth article about the future of technology, covering recent advancements and future predictions..."
				}
				""";

		for (int i = 0; i < 5; i++) {
			adapter.apply(LdiAdapter.Content.of(content, Lang.JSONLD.getHeaderString())).toList();
		}

		// verify that with context, caching is present
		WireMock.verify(1, getRequestedFor(urlEqualTo("/context.jsonld")));

		final var adapterWithoutCaching = new RdfAdapter(RIOT.getContext());
		for (int i = 0; i < 5; i++) {
			adapterWithoutCaching.apply(LdiAdapter.Content.of(content, Lang.JSONLD.getHeaderString())).toList();
		}

		// verify that without context, no caching is present
		WireMock.verify(6, getRequestedFor(urlEqualTo("/context.jsonld")));
	}

}
