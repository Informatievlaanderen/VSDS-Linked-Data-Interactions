package be.vlaanderen.informatievlaanderen.ldes.ldio.util;

import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.MemberIdNotFoundException;
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

class MemberIdExtractorTest {
	private final MemberIdExtractor memberIdExtractor = new MemberIdExtractor();

	@Test
	void when_ModelContainsMember_then_MemberIdIsExtracted() throws IOException, URISyntaxException {
		Model model = RDFParser.fromString(readFile("original.jsonld")).lang(Lang.JSONLD11).toModel();

		String memberId = memberIdExtractor.extractMemberId(model);

		assertEquals("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228651/628", memberId);
	}

	@Test
	void when_ModelNotContainsMember_then_MemberIdNotFoundExceptionIsThrown() throws IOException, URISyntaxException {
		Model model = RDFParser.fromString(readFile("original-without-member.jsonld")).lang(Lang.JSONLD11).toModel();

		MemberIdNotFoundException memberIdNotFoundException = assertThrows(MemberIdNotFoundException.class,
				() -> memberIdExtractor.extractMemberId(model));

		assertEquals(
				"Could not extract https://w3id.org/tree#member statement from [ a       <http://data.europa.eu/m8g/PeriodOfTime> ;\n"
						+
						"  <http://data.europa.eu/m8g/endTime>\n" +
						"          \"2020-12-10T19:00:00Z\"^^<http://www.w3.org/2001/XMLSchema#dateTime> ;\n" +
						"  <http://data.europa.eu/m8g/startTime>\n" +
						"          \"2020-12-05T05:00:00Z\"^^<http://www.w3.org/2001/XMLSchema#dateTime>\n" +
						"] .\n",
				memberIdNotFoundException.getMessage());
	}

	private String readFile(String fileName)
			throws URISyntaxException, IOException {
		File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).toURI());
		return Files.lines(Paths.get(file.toURI())).collect(Collectors.joining("\n"));
	}

}