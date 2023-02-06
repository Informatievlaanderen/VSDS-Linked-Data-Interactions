package be.vlaanderen.informatievlaanderen.ldes.ldto.types;

import org.apache.jena.rdf.model.Model;

public interface LdtoOutput {
	void sendLinkedData(Model linkedDataModel);
}
