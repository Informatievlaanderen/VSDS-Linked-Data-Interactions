package be.vlaanderen.informatievlaanderen.ldes.ldi.types;

import org.apache.jena.rdf.model.Model;

import java.util.function.Function;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter.InputObject;

public interface LdiAdapter extends LdiComponent, Function<InputObject, Stream<Model>> {
	record InputObject(String content, String contentType) {
		public static InputObject of(String content, String contentType) {
			return new InputObject(content, contentType);
		}
	}
}
