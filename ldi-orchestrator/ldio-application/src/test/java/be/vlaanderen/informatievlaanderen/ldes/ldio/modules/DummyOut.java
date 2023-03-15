package be.vlaanderen.informatievlaanderen.ldes.ldio.modules;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;

import java.util.ArrayList;
import java.util.List;

public class DummyOut implements LdiOutput {
	public List<Model> output = new ArrayList<>();

	@Override
	public void accept(Model model) {
		this.output.add(model);
	}
}
