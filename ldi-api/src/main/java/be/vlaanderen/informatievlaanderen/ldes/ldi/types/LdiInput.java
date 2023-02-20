package be.vlaanderen.informatievlaanderen.ldes.ldi.types;

import org.apache.jena.rdf.model.Model;

public interface LdiInput extends LdiComponent {
	void passLinkedData(Model linkedDataModel);
}
