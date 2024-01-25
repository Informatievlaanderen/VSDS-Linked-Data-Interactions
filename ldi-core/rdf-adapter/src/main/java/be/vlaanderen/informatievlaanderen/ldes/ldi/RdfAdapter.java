package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.sparql.util.Context;

import java.util.stream.Stream;

import static org.apache.jena.riot.RDFLanguages.nameToLang;

public class RdfAdapter implements LdiAdapter {

	private final Context context;

    public RdfAdapter(Context context) {
        this.context = context;
    }

    @Override
	public Stream<Model> apply(Content input) {
		return Stream.of(
				RDFParser
						.fromString(input.content())

						.context(context)
						.lang(nameToLang(input.mimeType()))
						.toModel()
		);
	}

}
