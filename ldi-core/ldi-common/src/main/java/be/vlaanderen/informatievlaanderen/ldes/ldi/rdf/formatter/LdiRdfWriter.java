package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriterBuilder;

import java.io.OutputStream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.PrefixAdder.addPrefixesToModel;
import static org.apache.commons.lang3.StringUtils.isBlank;

public interface LdiRdfWriter {

	RDFWriterBuilder builder();

	/**
	 * @param model Model to be parsed to a String
	 * @return String representation of the RDF Jena model in the RDF format provided in the LdiRdfWriterProperties
	 */
	default String write(Model model) {
		return builder().source(addPrefixesToModel(model)).asString();
	}

	/**
	 * Parses an RDF Jena model in the RDF format provided in the LdiRdfWriterProperties to an output stream
	 *
	 * @param model        Model to be parsed
	 * @param outputStream output stream whereto the model needs to be written
	 */
	default void write(Model model, OutputStream outputStream) {
		builder().source(addPrefixesToModel(model)).output(outputStream);
	}

	static LdiRdfWriter getRdfWriter(LdiRdfWriterProperties properties) {
		if (Lang.JSONLD.equals(properties.getLang())) {
			return isBlank(properties.getJsonLdFrame())
					? new JsonLdPrettyWriter()
					: new JsonLdFrameWriter(properties);
		} else {
			return new GenericRdfWriter(properties);
		}
	}
}
