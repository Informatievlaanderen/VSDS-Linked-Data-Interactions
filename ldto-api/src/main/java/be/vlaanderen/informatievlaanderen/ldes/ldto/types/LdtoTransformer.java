package be.vlaanderen.informatievlaanderen.ldes.ldto.types;

import org.apache.jena.rdf.model.Model;

public interface LdtoTransformer {
	Model execute(Model linkedDataModel);
}
