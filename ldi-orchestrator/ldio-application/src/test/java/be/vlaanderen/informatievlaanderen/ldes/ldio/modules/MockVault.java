package be.vlaanderen.informatievlaanderen.ldes.ldio.modules;

import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class MockVault {
	private final List<Model> receivedObjects = new LinkedList<>();

	public void receiveData(Model object) {
		this.receivedObjects.add(object);
	}

	public List<Model> getReceivedObjects() {
		return receivedObjects;
	}
}
