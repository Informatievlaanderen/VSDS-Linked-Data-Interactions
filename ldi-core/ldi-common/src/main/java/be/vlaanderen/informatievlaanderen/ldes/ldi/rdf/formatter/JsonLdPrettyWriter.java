package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.PrefixAdder.addPrefixesToModel;

public class JsonLdPrettyWriter implements LdiRdfWriter {

	@Override
	public String write(Model model) {
		return RDFWriter.source(addPrefixesToModel(model))
				.format(RDFFormat.JSONLD10_PRETTY)
				.asString();
	}

}
