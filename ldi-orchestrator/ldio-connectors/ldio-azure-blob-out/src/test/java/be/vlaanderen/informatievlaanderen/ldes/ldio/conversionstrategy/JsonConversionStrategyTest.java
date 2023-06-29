package be.vlaanderen.informatievlaanderen.ldes.ldio.conversionstrategy;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class JsonConversionStrategyTest {

	private final ConversionStrategy conversionStrategy = new JsonConversionStrategy(
			"https://essentialcomplexity.eu/gipod.jsonld");

	@Test
	void when_ModelIsConverted_then_JsonSerializedStringIsReturned() throws IOException, URISyntaxException {
		Model model = RDFParser.fromString(readFile("original.jsonld")).lang(Lang.JSONLD11).toModel();
		String expectedJson = readFile("expected.json");

		String actualJson = conversionStrategy.getContent(model);

		assertEquals(replaceAnonymousIdsAndLineSeparators(expectedJson),
				replaceAnonymousIdsAndLineSeparators(actualJson));

	}

	@Test
	void when_FileExtensionIsAsked_then_JsonIsReturned() {
		assertEquals("json", conversionStrategy.getFileExtension());
	}

	private static String replaceAnonymousIdsAndLineSeparators(String expectedJson) {
		return expectedJson
				.replaceAll("_:b\\d", "") // Anonymous Ids
				.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")); // Line Separators
	}

	private String readFile(String fileName)
			throws URISyntaxException, IOException {
		File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).toURI());
		return Files.lines(Paths.get(file.toURI())).collect(Collectors.joining("\n"));
	}

}