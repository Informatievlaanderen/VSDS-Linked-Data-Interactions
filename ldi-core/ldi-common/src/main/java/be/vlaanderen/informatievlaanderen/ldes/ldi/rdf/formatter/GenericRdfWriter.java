package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.*;

import java.io.OutputStream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.PrefixAdder.*;

public class GenericRdfWriter implements LdiRdfWriter {
	private final RDFWriterBuilder rdfWriter;

	public GenericRdfWriter(LdiRdfWriterProperties properties) {
		this.rdfWriter = RDFWriter.create().lang(properties.getLang());
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
