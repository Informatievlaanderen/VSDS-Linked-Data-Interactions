package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.riot.RDFWriterBuilder;

import java.io.OutputStream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.PrefixAdder.addPrefixesToModel;

public class JsonLdPrettyWriter implements LdiRdfWriter {
	private final RDFWriterBuilder rdfWriter;

	public JsonLdPrettyWriter() {
		this.rdfWriter = RDFWriter.create().format(RDFFormat.JSONLD_PRETTY);
	}

	@Override
	public String getContentType() {
		return Lang.JSONLD.getHeaderString();
	}

    @Override
	public String write(Model model) {
		return rdfWriter.source(addPrefixesToModel(model)).asString();
	}

	@Override
	public void writeToOutputStream(Model model, OutputStream outputStream) {
		rdfWriter.source(addPrefixesToModel(model)).output(outputStream);
	}

}
