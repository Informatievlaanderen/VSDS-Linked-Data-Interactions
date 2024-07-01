package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.SparqlConstructTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioTransformer;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;

public class LdioSparqlConstruct extends LdioTransformer {
	public static final String NAME = "Ldio:SparqlConstructTransformer";
	private final SparqlConstructTransformer transformer;

	public LdioSparqlConstruct(Query query, boolean inferMode) {
		transformer = new SparqlConstructTransformer(query, inferMode);
	}

	@Override
	public void apply(Model model) {
		transformer.transform(model).forEach(this::next);
	}
}
