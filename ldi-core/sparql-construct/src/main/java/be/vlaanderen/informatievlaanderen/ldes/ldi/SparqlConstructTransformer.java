package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SparqlConstructTransformer implements LdiTransformer {

	private final Query query;
	private final boolean includeOriginal;

	public SparqlConstructTransformer(Query query, boolean includeOriginal) {
		this.query = query;
		this.includeOriginal = includeOriginal;
	}

	@Override
	public List<Model> apply(Model linkedDataModel) {
		final Dataset dataset = queryDataset(linkedDataModel);
		final List<Model> result = extractModelsFromDataset(dataset);
		handleIncludeOriginal(result, linkedDataModel);
		return result;
	}

	private Dataset queryDataset(Model linkedDataModel) {
		final var dataset = DatasetFactory.create(linkedDataModel);
		try (QueryExecution qexec = QueryExecutionFactory.create(query, dataset)) {
			return qexec.execConstructDataset();
		}
	}

	private void handleIncludeOriginal(List<Model> result, Model linkedDataModel) {
		if (includeOriginal && result.size() == 1) {
			result.get(0).add(linkedDataModel);
		}
	}

	private List<Model> extractModelsFromDataset(Dataset dataset) {

		if (hasMultipleModels(dataset)) {
			return splitDataset(dataset);
		} else {
			return List.of(dataset.getDefaultModel());
		}

    }

	private boolean hasMultipleModels(Dataset dataset) {
		return dataset.listNames().hasNext();
	}

	private List<Model> splitDataset(Dataset dataset) {
		List<Model> result = new ArrayList<>();
		Iterator<Resource> it = dataset.listModelNames();
		while (it.hasNext()) {
			Model namedModel = dataset.getNamedModel(it.next());
			namedModel.add(dataset.getDefaultModel());
			result.add(namedModel);
		}

		if (result.isEmpty()) {
			result.add(dataset.getDefaultModel());
		}

		return result;
	}

}
