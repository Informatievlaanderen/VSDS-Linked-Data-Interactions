package be.vlaanderen.informatievlaanderen.ldes.ldto.services;

import org.apache.jena.rdf.model.Model;

public interface ComponentExecutor {
	void transformLinkedData(Model linkedDataModel);
}
