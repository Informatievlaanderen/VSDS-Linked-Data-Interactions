package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.datasetsplitter.DatasetSplitter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sparqlfunctions.*;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOneToManyTransformer;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.function.FunctionRegistry;

import java.util.List;

/**
 * Will modify the model based on the given SPARQL Construct Query
 */
public class SparqlConstructTransformer implements LdiOneToManyTransformer {

	private final Query query;
	private final boolean includeOriginal;
	private final DatasetSplitter datasetSplitter;

	public SparqlConstructTransformer(Query query, boolean includeOriginal, DatasetSplitter datasetSplitter) {
		this.query = query;
		this.includeOriginal = includeOriginal;
		this.datasetSplitter = datasetSplitter;
		initGeoFunctions();
	}

	@Override
	public List<Model> transform(Model linkedDataModel) {
		final Dataset dataset = queryDataset(linkedDataModel);
		final List<Model> result = datasetSplitter.split(dataset);
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
			result.getFirst().add(linkedDataModel);
		}
	}

	private void initGeoFunctions() {
		FunctionRegistry functionRegistry = FunctionRegistry.get();
		functionRegistry.put(FirstCoordinate.NAME, FirstCoordinate.class);
		functionRegistry.put(LastCoordinate.NAME, LastCoordinate.class);
		functionRegistry.put(LineLength.NAME, LineLength.class);
		functionRegistry.put(MidPoint.NAME, MidPoint.class);
		functionRegistry.put(PointAtFromStart.NAME, PointAtFromStart.class);
		functionRegistry.put(DistanceFromStart.NAME, DistanceFromStart.class);
		functionRegistry.put(LineAtIndex.NAME, LineAtIndex.class);
	}
}
