package be.vlaanderen.informatievlaanderen.ldes.ldto.types;

import org.apache.jena.rdf.model.Model;

import java.util.Map;

public interface LdtoOutput {
	void init(Map<String, String> config);
	void sendLinkedData(Model linkedDataModel);
}
