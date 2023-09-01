package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.apache.jena.rdf.model.Model;

import java.util.stream.Stream;

public class ModelSplitAdapter implements LdiAdapter {

	private final String subjectType;
	private final LdiAdapter ldiAdapter;
	private final ModelSplitter modelSplitter;

	public ModelSplitAdapter(String subjectType, LdiAdapter ldiAdapter, ModelSplitter modelSplitter) {
		this.subjectType = subjectType;
		this.ldiAdapter = ldiAdapter;
		this.modelSplitter = modelSplitter;
	}

	@Override
	public Stream<Model> apply(Content content) {
		return ldiAdapter.apply(content).flatMap(model -> modelSplitter.split(model, subjectType).stream());
	}

}
