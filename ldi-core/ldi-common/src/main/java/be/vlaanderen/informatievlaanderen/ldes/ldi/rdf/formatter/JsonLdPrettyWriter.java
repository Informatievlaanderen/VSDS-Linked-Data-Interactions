package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.riot.RDFWriterBuilder;

public class JsonLdPrettyWriter implements LdiRdfWriter {

	@Override
	public RDFWriterBuilder builder() {
		return RDFWriter.create().format(RDFFormat.JSONLD10_PRETTY);
	}

}
