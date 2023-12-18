package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import org.apache.jena.riot.RDFWriter;
import org.apache.jena.riot.RDFWriterBuilder;

public class GenericRdfWriter implements LdiRdfWriter {
	private final LdiRdfWriterProperties properties;

	public GenericRdfWriter(LdiRdfWriterProperties properties) {
		this.properties = properties;
	}

	@Override
	public RDFWriterBuilder builder() {
		return RDFWriter.create().lang(properties.getLang());
	}
}
