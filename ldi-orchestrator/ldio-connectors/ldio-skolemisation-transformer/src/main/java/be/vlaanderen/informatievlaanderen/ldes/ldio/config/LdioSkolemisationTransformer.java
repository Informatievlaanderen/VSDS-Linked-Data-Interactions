package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.SkolemisationTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioTransformer;
import org.apache.jena.rdf.model.Model;

public class LdioSkolemisationTransformer extends LdioTransformer {
	public static final String NAME = "Ldio:SkolemisationTransformer";
	private final SkolemisationTransformer skolemisationTransformer;

	public LdioSkolemisationTransformer(SkolemisationTransformer skolemisationTransformer) {
		this.skolemisationTransformer = skolemisationTransformer;
	}

	@Override
	public void apply(Model model) {
		this.next(skolemisationTransformer.transform(model));
	}
}
