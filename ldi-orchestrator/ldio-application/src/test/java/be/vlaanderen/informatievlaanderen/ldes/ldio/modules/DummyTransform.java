package be.vlaanderen.informatievlaanderen.ldes.ldio.modules;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOneToOneTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioTransformer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public class DummyTransform extends LdioTransformer {
	LdiOneToOneTransformer ldiTransformer = model -> {
		Resource subject = model.listSubjects().toList().get(0);
		model.add(model.createLiteralStatement(subject, model.createProperty("http://schema.org/description"),
				"Transformed"));
		return model;
	};

	@Override
	public void apply(Model model) {
		this.next(ldiTransformer.transform(model));
	}
}
