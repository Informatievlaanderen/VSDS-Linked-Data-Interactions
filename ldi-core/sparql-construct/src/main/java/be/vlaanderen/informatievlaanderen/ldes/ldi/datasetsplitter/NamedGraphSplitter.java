package be.vlaanderen.informatievlaanderen.ldes.ldi.datasetsplitter;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.iterator.WrappedIterator;

import java.util.List;

public class NamedGraphSplitter implements DatasetSplitter {

	NamedGraphSplitter() {
	}

	@Override
	public List<Model> split(Dataset dataset) {
		if (hasOnlyDefaultGraph(dataset)) {
			return List.of(dataset.getDefaultModel());
		}
		return splitByNamedGraph(dataset);
	}



	private static boolean hasOnlyDefaultGraph(Dataset dataset) {
		return !dataset.listNames().hasNext();
	}

	private static List<Model> splitByNamedGraph(Dataset dataset) {
		return WrappedIterator.create(dataset.listModelNames())
				.mapWith(name -> dataset.getNamedModel(name).add(dataset.getDefaultModel()))
				.toList();
	}
}
