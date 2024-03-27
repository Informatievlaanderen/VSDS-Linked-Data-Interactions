package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.JsonLDFrameException;
import jakarta.json.JsonObject;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.riot.RDFWriterBuilder;

import java.io.OutputStream;
import java.io.StringReader;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.JsonDocument;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.PrefixAdder.addPrefixesToModel;

public class JsonLdFrameWriter implements LdiRdfWriter {
	private final RDFWriterBuilder rdfWriter;
	private final JsonDocument frame;

	public JsonLdFrameWriter(JsonDocument frame) {
		this.frame = frame;
		this.rdfWriter = RDFWriter.create().format(RDFFormat.JSONLD);
	}

	public static JsonLdFrameWriter fromProperties(LdiRdfWriterProperties properties) throws JsonLdError {
		return new JsonLdFrameWriter(JsonDocument.of(new StringReader(properties.getJsonLdFrame())));
	}

    @Override
    public String getContentType() {
        return Lang.JSONLD.getHeaderString();
    }

    @Override
	public String write(Model model) {
	    JsonObject jsonObject;
	    try {
		    JsonDocument input = JsonDocument.of(new StringReader(rdfWriter.source(addPrefixesToModel(model)).asString()));
		    jsonObject = JsonLd.frame(input, frame).get();
	    } catch (JsonLdError e) {
		    throw new JsonLDFrameException(e);
	    }

	    return jsonObject.toString();
	}

	@Override
	public void writeToOutputStream(Model model, OutputStream outputStream) {
		rdfWriter.source(addPrefixesToModel(model)).output(outputStream);
	}
}
