package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.sparql.util.Context;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.apache.jena.riot.RDFLanguages.nameToLang;

/**
 * Basic adapter that will convert an RDF string to a linked data model
 */
public class RdfAdapter implements LdiAdapter {

	private final Context context;

    public RdfAdapter(Context context) {
        this.context = context;
    }

    @Override
	public Stream<Model> apply(Content input) {
		return Stream.of(
				RDFParser
						.source(new ByteArrayInputStream(input.content().getBytes(StandardCharsets.UTF_8)))
						.context(context)
						.lang(nameToLang(input.mimeType()))
						.toModel()
		);
	}

}
