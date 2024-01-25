package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import com.apicatalog.jsonld.JsonLdOptions;
import com.apicatalog.jsonld.context.cache.LruCache;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RIOT;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.ContextAccumulator;

import java.util.stream.Stream;

import static org.apache.jena.riot.RDFLanguages.nameToLang;
import static org.apache.jena.riot.lang.LangJSONLD11.JSONLD_OPTIONS;

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
