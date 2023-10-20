package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.JsonLDWriteContext;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.writer.JsonLD10Writer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

import com.github.jsonldjava.core.JsonLdOptions;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties.FRAME_TYPE;
import static org.junit.jupiter.api.Assertions.*;

public class LdiRdfWriterTest {
	@Test
	void formatModel_jsonLD() throws IOException, URISyntaxException {
		String input = getJsonString("./rdfFormatter/product.jsonld");

		Model model = RDFParser.fromString(input)
				.lang(Lang.JSONLD)
				.toModel();

		String frameType = "http://purl.org/goodrelations/v1#Offering";
		LdiRdfWriterProperties writerProperties = new LdiRdfWriterProperties(Map.of(FRAME_TYPE, frameType));

		String output = LdiRdfWriter.getRdfWriter(writerProperties.withLang(Lang.JSONLD)).write(model);

		JsonObject outputJson = JSON.parse(output);

		assertFalse(outputJson.hasKey("@graph"));
		assertEquals(9, outputJson.size());
	}

	@Test
	void formatModel_turtle() throws IOException, URISyntaxException {
		String input = getJsonString("./rdfFormatter/product.jsonld");

		Model model = RDFParser.fromString(input)
				.lang(Lang.JSONLD)
				.toModel();

		String output = LdiRdfWriter.getRdfWriter(new LdiRdfWriterProperties().withLang(Lang.TURTLE)).write(model);
		String expected = getJsonString("./rdfFormatter/expected/product.ttl");

		assertTrue(RDFParser.fromString(output)
				.lang(Lang.TURTLE)
				.toModel()
				.isIsomorphicWith(RDFParser.fromString(expected).lang(Lang.TURTLE).toModel()));
	}

	@Test
	void formatModel_nquads() throws IOException, URISyntaxException {
		String input = getJsonString("./rdfFormatter/product.jsonld");

		Model model = RDFParser.fromString(input)
				.lang(Lang.JSONLD)
				.toModel();

		String output = LdiRdfWriter.getRdfWriter(new LdiRdfWriterProperties().withLang(Lang.NQUADS)).write(model);
		String expected = getJsonString("./rdfFormatter/expected/product.nq");

		assertTrue(RDFParser.fromString(output)
				.lang(Lang.NQUADS)
				.toModel()
				.isIsomorphicWith(RDFParser.fromString(expected).lang(Lang.NQUADS).toModel()));
	}

	@Test
	void getFramedContext() throws URISyntaxException, IOException {
		Model model = RDFParser.fromString(getJsonString("./rdfFormatter/person.jsonld"))
				.lang(Lang.JSONLD)
				.toModel();
		String frameType = "http://schema.org/Person";

		JsonLDWriteContext context = (JsonLDWriteContext) JsonLdWriter.getFramedContext(model, frameType);

		JsonObject frameObject = JSON.parse((String) context.get(JsonLD10Writer.JSONLD_FRAME));
		assertEquals(frameType, frameObject.get("@type").getAsString().value());
		assertTrue(frameObject.hasKey("@context"));
		assertTrue(((JsonLdOptions) context.get(JsonLD10Writer.JSONLD_OPTIONS)).getOmitGraph());
	}

	private String getJsonString(String resource) throws URISyntaxException, IOException {
		File file = new File(
				Objects.requireNonNull(getClass().getClassLoader().getResource(resource)).toURI());
		return Files.readString(file.toPath());
	}
}
