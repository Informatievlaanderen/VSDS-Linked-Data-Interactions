package be.vlaanderen.informatievlaanderen.ldes.ldi.skolemisation;

import org.apache.jena.rdf.model.Model;

public class EmptySkolemizer implements Skolemizer {
	@Override
	public Model skolemize(Model model) {
		return model;
	}
}
