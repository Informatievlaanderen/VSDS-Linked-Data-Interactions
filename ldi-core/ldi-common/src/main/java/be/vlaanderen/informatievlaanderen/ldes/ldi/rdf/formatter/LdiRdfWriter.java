package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

public interface LdiRdfWriter {
	String write(Model model);

	static LdiRdfWriter getRdfWriter(LdiRdfWriterProperties properties) {
		if (Lang.JSONLD.equals(properties.getLang())) {
			return new JsonLdWriter(properties);
		} else {
			return new GenericRdfWriter(properties);
		}
	}
}
