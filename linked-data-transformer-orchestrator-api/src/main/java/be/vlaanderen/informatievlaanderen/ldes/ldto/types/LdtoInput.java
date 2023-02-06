package be.vlaanderen.informatievlaanderen.ldes.ldto.types;

import org.apache.jena.rdf.model.Model;

public interface LdtoInput {
	void passLinkedData(Model linkedDataModel);
}
