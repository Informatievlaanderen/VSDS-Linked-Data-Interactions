package be.vlaanderen.informatievlaanderen.ldes.ldio.modules;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import java.util.List;

public class DummyTransform implements LdiTransformer {
	@Override
	public List<Model> apply(Model model) {
		Resource subject = model.listSubjects().toList().get(0);
		model.add(model.createLiteralStatement(subject, model.createProperty("http://schema.org/description"),
				"Transformed"));
		return List.of(model);
	}
}
