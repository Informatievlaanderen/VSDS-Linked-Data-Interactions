package be.vlaanderen.informatievlaanderen.ldes.ldio.conversionstrategy;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RDFSerializationConversionStrategyTest {

	@ParameterizedTest(name = "OutputLang {0} results in the creation of ConversionStrategy {1}")
	@ArgumentsSource(RDFTypeAndFileArgumentsProvider.class)
	void when_ModelIsConverted_then_JsonSerializedStringIsReturned(Lang lang, String expectedContentFileName,
			String expectedFileExtension) throws IOException, URISyntaxException {
		ConversionStrategy conversionStrategy = new RDFSerializationConversionStrategy(lang);
		Model model = RDFParser.fromString(readFile("original.jsonld")).lang(Lang.JSONLD11).toModel();
		String expectedContent = readFile(expectedContentFileName);

		String actualContent = conversionStrategy.getContent(model);

		System.out.println(actualContent);
		assertTrue(RDFParser.fromString(expectedContent).lang(lang).toModel().isIsomorphicWith(
				RDFParser.fromString(actualContent).lang(lang).toModel()));
		assertEquals(expectedFileExtension, conversionStrategy.getFileExtension());

	}

	private String readFile(String fileName)
			throws URISyntaxException, IOException {
		File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).toURI());
		return Files.lines(Paths.get(file.toURI())).collect(Collectors.joining("\n"));
	}

	static class RDFTypeAndFileArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(Lang.NQUADS, "expected.nq", "nq"),
					Arguments.of(Lang.NTRIPLES, "expected.nt", "nt"),
					Arguments.of(Lang.TURTLE, "expected.ttl", "ttl"),
					Arguments.of(Lang.JSONLD, "expected.jsonld", "jsonld"));
		}
	}

}