package be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpointresponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EndpointResponseFactoryTest {

	private final static String basePath = "src/test/resources/endpointrequester/";

	private final EndpointResponseFactory endpointResponseFactory = new EndpointResponseFactory();

	@ParameterizedTest(name = "Response for RDFFormat: {0}")
	@ArgumentsSource(ContentTypeRdfFormatLangLdesMemberArgumentsProvider.class)
	void endpointResponseRDFFormats(Lang rdfFormat, String filePath) throws Exception {
		Model expected = RDFParser.source(basePath + "example-ldes.xml").toModel();
		InputStream inputStream = new FileInputStream(filePath);
		EndpointResponse response = endpointResponseFactory.createResponse(inputStream, rdfFormat);

		assertEquals(expected.getResource("uri"), response.model().getResource("uri"));
	}

	static class ContentTypeRdfFormatLangLdesMemberArgumentsProvider implements
			ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(Lang.NQUADS, basePath + "example-ldes.nq"),
					Arguments.of(Lang.NTRIPLES, basePath + "example-ldes.nq"),
					Arguments.of(Lang.JSONLD, basePath + "example-ldes.json"),
					Arguments.of(Lang.TURTLE, basePath + "example-ldes.nq"),
					Arguments.of(Lang.RDFJSON, basePath + "example-ldes-rdf-json.json"),
					Arguments.of(Lang.N3, basePath + "example-ldes.nq"),
					Arguments.of(Lang.TRIG, basePath + "example-ldes.nq"),
					Arguments.of(Lang.N3, basePath + "example-ldes.nq"),
					Arguments.of(Lang.NTRIPLES, basePath + "example-ldes.nq"),
					Arguments.of(Lang.RDFXML, basePath + "example-ldes.xml"),
					Arguments.of(Lang.JSONLD11, basePath + "example-ldes.json"),
					Arguments.of(Lang.JSONLD10, basePath + "example-ldes.json"),
					Arguments.of(Lang.N3, basePath + "example-ldes.nq"),
					// Arguments.o, Lang.TRIX, ""),
					Arguments.of(Lang.TURTLE, basePath + "example-ldes.nq"),
					Arguments.of(Lang.TRIG, basePath + "example-ldes.nq"));
		}
	}

}