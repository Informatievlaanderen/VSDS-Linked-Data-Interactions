package be.vlaanderen.informatievlaanderen.ldes.ldi.skolemisation;

import org.apache.jena.rdf.model.Model;

@FunctionalInterface
public interface Skolemizer {
	Model skolemize(Model model);
}
