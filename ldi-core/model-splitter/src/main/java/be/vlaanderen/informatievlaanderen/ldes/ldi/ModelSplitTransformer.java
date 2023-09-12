package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.jena.rdf.model.Model;

import java.util.Collection;

public class ModelSplitTransformer implements LdiTransformer {

	private final String subjectType;
	private final ModelSplitter modelSplitter;

	public ModelSplitTransformer(String subjectType, ModelSplitter modelSplitter) {
		this.subjectType = subjectType;
		this.modelSplitter = modelSplitter;
	}

	@Override
	public Collection<Model> apply(Model model) {
		return modelSplitter.split(model, subjectType);
	}

}
