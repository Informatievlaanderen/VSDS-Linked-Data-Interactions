package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.riot.RDFWriterBuilder;

import java.io.OutputStream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.PrefixAdder.addPrefixesToModel;

/**
 * Default implementation of the LdiRdfWriter
 */
public class GenericRdfWriter implements LdiRdfWriter {

	private final RDFWriterBuilder rdfWriter;
	private final String contentType;

	public GenericRdfWriter(LdiRdfWriterProperties properties) {
		contentType = properties.getLang().getHeaderString();
		this.rdfWriter = RDFWriter.create().lang(properties.getLang());
	}

	@Override
	public String getContentType() {
		return contentType;
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
