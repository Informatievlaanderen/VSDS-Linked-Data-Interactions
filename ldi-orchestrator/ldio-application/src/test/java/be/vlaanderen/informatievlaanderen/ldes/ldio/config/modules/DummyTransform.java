package be.vlaanderen.informatievlaanderen.ldes.ldio.config.modules;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public class DummyTransform implements LdiTransformer {
	@Override public Model apply(Model model) {
		Resource subject = model.listSubjects().toList().get(0);
		model.add(model.createLiteralStatement(subject, model.createProperty("http://schema.org/description"),
				"Transformed"));
		return model;
	}
}
