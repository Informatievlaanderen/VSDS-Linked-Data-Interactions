package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.JsonLDWriteContext;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.writer.JsonLD10Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import com.github.jsonldjava.core.JsonLdOptions;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties.FRAME;
import static org.assertj.core.api.Assertions.assertThat;

class LdiRdfWriterTest {

	@Test
	void formatModel_jsonLD() throws IOException, URISyntaxException {
		Model model = RDFParser.source("rdf/formatter/product.jsonld").lang(Lang.JSONLD).toModel();
		String frame = getFileContentString("rdf/formatter/product.frame.jsonld");
		Model expected = RDFParser.source("rdf/formatter/expected/product.jsonld").lang(Lang.JSONLD).toModel();
		LdiRdfWriterProperties writerProperties = new LdiRdfWriterProperties(Map.of(FRAME, frame));

		String output = LdiRdfWriter.getRdfWriter(writerProperties.withLang(Lang.JSONLD)).write(model);
		JsonObject outputJson = JSON.parse(output);
		Model outputModel = RDFParser.fromString(output).lang(Lang.JSONLD).toModel();

		assertThat(outputJson.hasKey("@graph")).isFalse();
		assertThat(outputModel).matches(expected::isIsomorphicWith);
	}

	@Test
	void formatModel_jsonLD_withoutFrame() {
		Model model = RDFParser.source("rdf/formatter/product.jsonld").lang(Lang.JSONLD).toModel();
		LdiRdfWriterProperties writerProperties = new LdiRdfWriterProperties().withLang(Lang.JSONLD);

		String output = LdiRdfWriter.getRdfWriter(writerProperties).write(model);
		JsonObject outputJson = JSON.parse(output);

		assertThat(outputJson).isNotNull();
	}

	@Test
	void formatModel_turtle() {
		Model inputModel = RDFParser.source("rdf/formatter/product.jsonld").lang(Lang.JSONLD).toModel();
		Model expectedModel = RDFParser.source("rdf/formatter/expected/product.ttl").lang(Lang.TURTLE).toModel();

		String output = LdiRdfWriter.getRdfWriter(new LdiRdfWriterProperties().withLang(Lang.TURTLE)).write(inputModel);
		Model outputModel = RDFParser.fromString(output).lang(Lang.TURTLE).toModel();

		assertThat(outputModel).matches(expectedModel::isIsomorphicWith);
	}

	@Test
	void formatModel_nquads() {
		Model model = RDFParser.source("rdf/formatter/product.jsonld").lang(Lang.JSONLD).toModel();
		Model expectedModel = RDFParser.source("rdf/formatter/expected/product.nq").lang(Lang.NQUADS).toModel();

		String output = LdiRdfWriter.getRdfWriter(new LdiRdfWriterProperties().withLang(Lang.NQUADS)).write(model);
		Model outputModel = RDFParser.fromString(output).lang(Lang.NQUADS).toModel();

		assertThat(outputModel).matches(expectedModel::isIsomorphicWith);
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

		assertThat(frameObject)
				.matches(json -> json.hasKey("@type"))
				.matches(json -> json.hasKey("@context"));
		assertThat(((JsonLdOptions) context.get(JsonLD10Writer.JSONLD_OPTIONS)).getOmitGraph())
				.isTrue();
	}

	@ParameterizedTest
	@ArgumentsSource(LangProvider.class)
	void test_WritingWithByteArrayStream(Lang outputLang) {
		Model model = RDFParser.source("rdf/formatter/product.jsonld").lang(Lang.JSONLD).toModel();
		Model expectedModel = RDFParser.source("rdf/formatter/expected/product.nq").lang(Lang.NQUADS).toModel();

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		LdiRdfWriter.getRdfWriter(new LdiRdfWriterProperties().withLang(outputLang)).writeToOutputStream(model, byteArrayOutputStream);
		byte[] modelBytes = byteArrayOutputStream.toByteArray();
		Model outputModel = RDFParser.source(new ByteArrayInputStream(modelBytes)).lang(outputLang).toModel();

		assertThat(outputModel).matches(expectedModel::isIsomorphicWith);
	}

	private String getFileContentString(String resource) throws URISyntaxException, IOException {
		File file = new File(
				Objects.requireNonNull(getClass().getClassLoader().getResource(resource)).toURI());
		return Files.readString(file.toPath());
	}

	static class LangProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(Lang.RDFPROTO, Lang.RDFTHRIFT, Lang.TURTLE).map(Arguments::of);
		}
	}
}
