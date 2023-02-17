package be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpoint;

import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EndpointTest {

	@ParameterizedTest(name = "Endpoint for RDFFormat: {0}")
	@ArgumentsSource(RdfFormatLangArgumentsProvider.class)
	void getEndpointLang(String contentType, Lang rdfFormat) {
		Endpoint endpoint = new Endpoint("", rdfFormat);
		assertEquals(endpoint.contentType(), contentType);
	}

	@Test
	void getEndpointLang_WhenLangNull() {
		Endpoint endpoint = new Endpoint("", null);
		assertEquals("", endpoint.contentType());
	}

	static class RdfFormatLangArgumentsProvider implements
			ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("application/n-quads", Lang.NQUADS),
					Arguments.of("application/n-triples", Lang.NTRIPLES),
					Arguments.of("application/ld+json", Lang.JSONLD),
					Arguments.of("text/turtle", Lang.TURTLE),
					Arguments.of("application/rdf+json", Lang.RDFJSON),
					Arguments.of("application/trig", Lang.TRIG),
					Arguments.of("application/rdf+xml", Lang.RDFXML),
					Arguments.of("x/ld-json-11", Lang.JSONLD11),
					Arguments.of("x/ld-json-10", Lang.JSONLD10),
					Arguments.of("text/rdf+n3", Lang.N3),
					Arguments.of("application/trix", Lang.TRIX));
		}
	}

}