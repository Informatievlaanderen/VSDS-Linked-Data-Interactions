package be.vlaanderen.informatievlaanderen.ldes.ldio.types;

import org.apache.jena.rdf.model.Model;

import java.util.List;

public abstract class LdioTransformer {

	private LdioTransformer nextProcessor;

	public abstract void apply(Model model);

	protected void next(Model model) {
		if (nextProcessor != null) {
			nextProcessor.apply(model);
		}
	}

	public static LdioTransformer link(LdioTransformer first, List<LdioTransformer> chain) {
		LdioTransformer head = first;
		for (LdioTransformer nextInChain : chain) {
			head.nextProcessor = nextInChain;
			head = nextInChain;
		}
		return first;
	}

}
