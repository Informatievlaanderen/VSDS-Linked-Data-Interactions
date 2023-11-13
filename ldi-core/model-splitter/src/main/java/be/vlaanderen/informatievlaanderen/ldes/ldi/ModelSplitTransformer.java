package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOneToManyTransformer;
import org.apache.jena.rdf.model.Model;

import java.util.List;

public class ModelSplitTransformer implements LdiOneToManyTransformer {

	private final String subjectType;
	private final ModelSplitter modelSplitter;

	public ModelSplitTransformer(String subjectType) {
		this.subjectType = subjectType;
		this.modelSplitter = new ModelSplitter();
	}

	@Override
	public List<Model> transform(Model model) {
		return modelSplitter.split(model, subjectType);
	}
}
