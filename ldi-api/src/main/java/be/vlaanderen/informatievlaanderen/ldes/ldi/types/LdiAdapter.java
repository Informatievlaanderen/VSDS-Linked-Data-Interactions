package be.vlaanderen.informatievlaanderen.ldes.ldi.types;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.function.Function;
import java.util.stream.Stream;

public class LdiAdapter implements Function<String, Stream<Model>> {
	@Override
	public Stream<Model> apply(String input) {
		return Stream.of(ModelFactory.createDefaultModel(), ModelFactory.createDefaultModel());
	}
}
