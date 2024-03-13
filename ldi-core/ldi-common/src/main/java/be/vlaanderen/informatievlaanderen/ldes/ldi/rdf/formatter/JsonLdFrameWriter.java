package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.JsonDocument;
import jakarta.json.JsonObject;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.riot.RDFWriterBuilder;

import java.io.OutputStream;
import java.io.StringReader;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.PrefixAdder.addPrefixesToModel;

public class JsonLdFrameWriter implements LdiRdfWriter {
	private final RDFWriterBuilder rdfWriter;
	private final JsonDocument frame;

	public JsonLdFrameWriter(LdiRdfWriterProperties properties) throws JsonLdError {
		frame = JsonDocument.of(new StringReader(properties.getJsonLdFrame()));
		this.rdfWriter = RDFWriter.create().format(RDFFormat.JSONLD);
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
		    throw new RuntimeException(e);
	    }

	    return jsonObject.toString();
	}

	@Override
	public void writeToOutputStream(Model model, OutputStream outputStream) {
		rdfWriter.source(addPrefixesToModel(model)).output(outputStream);
	}
}
