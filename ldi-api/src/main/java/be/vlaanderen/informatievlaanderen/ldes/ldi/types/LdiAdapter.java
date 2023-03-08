package be.vlaanderen.informatievlaanderen.ldes.ldi.types;

import org.apache.jena.rdf.model.Model;

import java.util.function.Function;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter.Content;

/**
 * The LDI Adapter allows a user to transform its received or generated content
 * into a linked data model (RDF).
 */
public interface LdiAdapter extends LdiComponent, Function<Content, Stream<Model>> {
	/**
	 * Represents the input of a LDI Adapter
	 * @param content Received content represented as a String
	 * @param mimeType Mime type of received data
	 */
	record Content(String content, String mimeType) {
		public static Content of(String content, String mimeType) {
			return new Content(content, mimeType);
		}
	}
}
