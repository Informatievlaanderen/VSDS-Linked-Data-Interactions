package be.vlaanderen.informatievlaanderen.ldes.ldi.types;

import org.apache.jena.rdf.model.Model;

public interface LdiTransformer extends LdiComponent {
	Model transform(Model linkedDataModel);
}
