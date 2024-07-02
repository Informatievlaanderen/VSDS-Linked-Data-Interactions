package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;

public class DummyOut implements LdiOutput {
	private final MockVault mockVault;

	public DummyOut(MockVault mockVault) {
		this.mockVault = mockVault;
	}

	@Override
	public void accept(Model model) {
		this.mockVault.receiveData(model);
	}
}
