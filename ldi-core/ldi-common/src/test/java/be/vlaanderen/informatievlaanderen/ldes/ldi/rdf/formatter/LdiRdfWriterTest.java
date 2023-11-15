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

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties.FRAME;
import static org.junit.jupiter.api.Assertions.*;

public class LdiRdfWriterTest {

	@Test
	void formatModel_jsonLD() throws IOException, URISyntaxException {
		String input = getFileContentString("rdf/formatter/product.jsonld");

		Model model = RDFParser.fromString(input)
				.lang(Lang.JSONLD)
				.toModel();

		String frame = getFileContentString("rdf/formatter/product.frame.jsonld");
		Model expected = RDFParser.fromString(getFileContentString("rdf/formatter/expected/product.jsonld"))
				.lang(Lang.JSONLD).toModel();

		LdiRdfWriterProperties writerProperties = new LdiRdfWriterProperties(Map.of(FRAME, frame));

		String output = LdiRdfWriter.getRdfWriter(writerProperties.withLang(Lang.JSONLD)).write(model);

		JsonObject outputJson = JSON.parse(output);
		Model outputModel = RDFParser.fromString(output).lang(Lang.JSONLD).toModel();

		assertFalse(outputJson.hasKey("@graph"));
		assertTrue(outputModel.isIsomorphicWith(expected));
	}

	@Test
	void formatModel_jsonLD_withoutFrame() throws IOException, URISyntaxException {
		String input = getFileContentString("rdf/formatter/product.jsonld");

		Model model = RDFParser.fromString(input)
				.lang(Lang.JSONLD)
				.toModel();

		LdiRdfWriterProperties writerProperties = new LdiRdfWriterProperties();

		String output = LdiRdfWriter.getRdfWriter(writerProperties.withLang(Lang.JSONLD)).write(model);

		JsonObject outputJson = JSON.parse(output);

		assertNotNull(outputJson);
	}

	@Test
	void formatModel_turtle() throws IOException, URISyntaxException {
		String input = getFileContentString("rdf/formatter/product.jsonld");

		Model model = RDFParser.fromString(input)
				.lang(Lang.JSONLD)
				.toModel();

		String output = LdiRdfWriter.getRdfWriter(new LdiRdfWriterProperties().withLang(Lang.TURTLE)).write(model);
		String expected = getFileContentString("rdf/formatter/expected/product.ttl");

		assertTrue(RDFParser.fromString(output)
				.lang(Lang.TURTLE)
				.toModel()
				.isIsomorphicWith(RDFParser.fromString(expected).lang(Lang.TURTLE).toModel()));
	}

	@Test
	void formatModel_nquads() throws IOException, URISyntaxException {
		String input = getFileContentString("rdf/formatter/product.jsonld");

		Model model = RDFParser.fromString(input)
				.lang(Lang.JSONLD)
				.toModel();

		String output = LdiRdfWriter.getRdfWriter(new LdiRdfWriterProperties().withLang(Lang.NQUADS)).write(model);
		String expected = getFileContentString("rdf/formatter/expected/product.nq");

		assertTrue(RDFParser.fromString(output)
				.lang(Lang.NQUADS)
				.toModel()
				.isIsomorphicWith(RDFParser.fromString(expected).lang(Lang.NQUADS).toModel()));
	}

	@Test
	void getFramedContext() {
		String frame = """
				{
				  "@context": "http://schema.org/",
				  "@type": "Person"
				}
				""";

		JsonLDWriteContext context = (JsonLDWriteContext) JsonLdFrameWriter.getFramedContext(frame);

		JsonObject frameObject = JSON.parse((String) context.get(JsonLD10Writer.JSONLD_FRAME));
		assertTrue(frameObject.hasKey("@type"));
		assertTrue(frameObject.hasKey("@context"));
		assertTrue(((JsonLdOptions) context.get(JsonLD10Writer.JSONLD_OPTIONS)).getOmitGraph());
	}

	private String getFileContentString(String resource) throws URISyntaxException, IOException {
		File file = new File(
				Objects.requireNonNull(getClass().getClassLoader().getResource(resource)).toURI());
		return Files.readString(file.toPath());
	}
}
