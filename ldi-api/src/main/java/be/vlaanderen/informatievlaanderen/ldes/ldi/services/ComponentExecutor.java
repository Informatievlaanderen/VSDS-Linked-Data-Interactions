package be.vlaanderen.informatievlaanderen.ldes.ldi.services;

import org.apache.jena.rdf.model.Model;

public interface ComponentExecutor {
	void transformLinkedData(Model linkedDataModel);
}
