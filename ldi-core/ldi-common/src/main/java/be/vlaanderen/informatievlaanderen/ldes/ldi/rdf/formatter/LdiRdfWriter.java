package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

import java.io.OutputStream;

import static org.apache.commons.lang3.StringUtils.isBlank;

public interface LdiRdfWriter {

	String getContentType();

	String write(Model model);

	void writeToOutputStream(Model model, OutputStream outputStream);

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
