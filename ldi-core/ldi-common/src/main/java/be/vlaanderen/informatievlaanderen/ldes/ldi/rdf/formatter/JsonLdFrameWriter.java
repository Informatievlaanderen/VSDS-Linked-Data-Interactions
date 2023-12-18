package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.riot.JsonLDWriteContext;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.riot.RDFWriterBuilder;
import org.apache.jena.riot.writer.JsonLD10Writer;
import org.apache.jena.sparql.util.Context;

import com.github.jsonldjava.core.JsonLdOptions;

public class JsonLdFrameWriter implements LdiRdfWriter {
	private final String frame;

	public JsonLdFrameWriter(LdiRdfWriterProperties properties) {
		this.frame = properties.getJsonLdFrame();
	}

	@Override
	public RDFWriterBuilder builder() {
		return RDFWriter.create()
				.context(getFramedContext(frame))
				.format(RDFFormat.JSONLD10_FRAME_PRETTY);
	}

	protected static Context getFramedContext(String context) {
		JsonLDWriteContext jenaCtx = new JsonLDWriteContext();

		JsonObject frame = JSON.parse(context);
		jenaCtx.set(JsonLD10Writer.JSONLD_FRAME, frame.toString());

		JsonLdOptions jsonLdOptions = new JsonLdOptions();
		jsonLdOptions.setOmitGraph(true);
		jenaCtx.setOptions(jsonLdOptions);

		return jenaCtx;
	}
}
