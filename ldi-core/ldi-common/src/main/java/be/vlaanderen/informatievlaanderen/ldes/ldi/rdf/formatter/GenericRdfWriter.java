package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.*;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.PrefixAdder.*;

public class GenericRdfWriter implements LdiRdfWriter {
	private final LdiRdfWriterProperties properties;

	public GenericRdfWriter(LdiRdfWriterProperties properties) {
		this.properties = properties;
	}

	@Override
	public String write(Model model) {
		return RDFWriter.source(addPrefixesToModel(model)).lang(properties.getLang()).asString();
	}
}
