package be.vlaanderen.informatievlaanderen.ldes.ldto.types;

import org.apache.jena.rdf.model.Model;

import java.util.Map;

public interface LdtoTransformer {
	void init(Map<String, String> config);
	Model execute(Model linkedDataModel);
}
