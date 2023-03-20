package be.vlaanderen.informatievlaanderen.ldes.ldio.modules;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;

import java.util.ArrayList;
import java.util.List;

public class DummyOut implements LdiOutput {
	public List<Model> output = new ArrayList<>();

	private final MockVault mockVault;

	public DummyOut(MockVault mockVault) {
		this.mockVault = mockVault;
	}

	@Override
	public void accept(Model model) {
		this.mockVault.receiveData(model);
	}
}
