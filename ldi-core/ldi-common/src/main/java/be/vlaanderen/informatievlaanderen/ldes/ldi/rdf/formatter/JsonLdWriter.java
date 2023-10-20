package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import com.github.jsonldjava.core.JsonLdOptions;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.JsonLDWriteContext;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.riot.writer.JsonLD10Writer;
import org.apache.jena.sparql.util.Context;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.PrefixAdder.addPrefixesToModel;

public class JsonLdWriter implements LdiRdfWriter {
	private final String frameType;

	public JsonLdWriter(LdiRdfWriterProperties properties) {
		this.frameType = properties.getFrameType();
	}

	@Override
	public String write(Model model) {
		return RDFWriter.source(addPrefixesToModel(model))
				.context(getFramedContext(model, frameType))
				.format(RDFFormat.JSONLD10_FRAME_PRETTY)
				.asString();
	}

	protected static Context getFramedContext(Model model, String frameType) {
		JsonLDWriteContext jenaCtx = new JsonLDWriteContext();

		JsonObject context = new JsonObject();
		model.getGraph().getPrefixMapping().getNsPrefixMap().forEach(context::put);

		JsonObject frame = new JsonObject();
		frame.put("@context", context);
		frame.put("@type", frameType);
		jenaCtx.set(JsonLD10Writer.JSONLD_FRAME, frame.toString());

		JsonLdOptions jsonLdOptions = new JsonLdOptions();
		jsonLdOptions.setOmitGraph(true);
		jenaCtx.setOptions(jsonLdOptions);

		return jenaCtx;
	}
}
