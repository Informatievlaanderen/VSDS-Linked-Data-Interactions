package be.vlaanderen.informatievlaanderen.ldes.ldio.config.modules;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;

public class DummyOut implements LdiOutput {
	public Model output;

	@Override
	public void accept(Model model) {
		this.output = model;
	}
}
