package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParserBuilder;

import java.util.stream.Stream;

import static org.apache.jena.riot.RDFLanguages.nameToLang;

public class RdfAdapter implements LdiAdapter {

	@Override
	public Stream<Model> apply(InputObject input) {
		return Stream.of(
				RDFParserBuilder.create().fromString(input.content()).lang(nameToLang(input.contentType())).toModel());
	}
}
