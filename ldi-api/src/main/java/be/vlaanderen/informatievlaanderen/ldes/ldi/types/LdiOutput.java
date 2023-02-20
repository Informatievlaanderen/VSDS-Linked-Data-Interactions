package be.vlaanderen.informatievlaanderen.ldes.ldi.types;

import org.apache.jena.rdf.model.Model;

public interface LdiOutput extends LdiComponent {
	void sendLinkedData(Model linkedDataModel);
}
