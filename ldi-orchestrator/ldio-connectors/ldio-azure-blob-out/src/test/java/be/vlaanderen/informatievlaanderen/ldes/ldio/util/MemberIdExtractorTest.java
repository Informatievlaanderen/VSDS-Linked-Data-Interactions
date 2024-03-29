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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

		assertThrows(MemberIdNotFoundException.class,
				() -> memberIdExtractor.extractMemberId(model));
	}

	private String readFile(String fileName)
			throws URISyntaxException, IOException {
		File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).toURI());
		return Files.lines(Paths.get(file.toURI())).collect(Collectors.joining("\n"));
	}

}