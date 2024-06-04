package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.JsonLDFrameException;
import com.apicatalog.jsonld.JsonLdError;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

import java.io.OutputStream;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Interface that is responsible for writing linked data away
 */
public interface LdiRdfWriter {

	/**
	 * @return the RDF format that will be used to write the linked data
	 */
	String getContentType();

	/**
	 * @param model RDF model that needs to be written
	 * @return string representation of the provided model
	 */
	String write(Model model);

	/**
	 * @param model        RDF model that needs to be written
	 * @param outputStream whereto the RDF model needs to be written
	 */
	void writeToOutputStream(Model model, OutputStream outputStream);

	static LdiRdfWriter getRdfWriter(LdiRdfWriterProperties properties) {
		if (Lang.JSONLD.equals(properties.getLang())) {
			try {
				return isBlank(properties.getJsonLdFrame())
						? new JsonLdPrettyWriter()
						: JsonLdFrameWriter.fromProperties(properties);
			} catch (JsonLdError e) {
				throw new JsonLDFrameException(e);
			}
		} else {
			return new GenericRdfWriter(properties);
		}
	}
}
