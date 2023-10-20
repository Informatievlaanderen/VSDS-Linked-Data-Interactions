package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import com.github.jsonldjava.core.JsonLdOptions;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.*;
import org.apache.jena.riot.writer.JsonLD10Writer;
import org.apache.jena.sparql.util.Context;

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
