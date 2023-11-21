package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.SparqlConstructTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioTransformer;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;

public class LdioSparqlConstruct extends LdioTransformer {
	private final SparqlConstructTransformer transformer;

	public LdioSparqlConstruct(Query query, boolean inferMode) {
		transformer = new SparqlConstructTransformer(query, inferMode);
	}

	@Override
	public void apply(Model model) {
		transformer.transform(model).forEach(this::next);
	}
}
