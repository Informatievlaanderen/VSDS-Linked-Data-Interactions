package be.vlaanderen.informatievlaanderen.ldes.ldio.types;

import org.apache.jena.rdf.model.Model;

import java.util.List;

public abstract class LdioProcessor {

	private LdioProcessor nextProcessor;

	public abstract void apply(Model model);

	protected void next(Model model) {
		if (nextProcessor != null) {
			nextProcessor.apply(model);
		}
	}

	public static LdioProcessor link(LdioProcessor first, List<LdioProcessor> chain) {
		LdioProcessor head = first;
		for (LdioProcessor nextInChain : chain) {
			head.nextProcessor = nextInChain;
			head = nextInChain;
		}
		return first;
	}

}
