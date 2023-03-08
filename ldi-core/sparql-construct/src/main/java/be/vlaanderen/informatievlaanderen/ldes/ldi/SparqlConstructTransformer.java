package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;

public class SparqlConstructTransformer implements LdiTransformer {
	private final Query query;
	private final boolean includeOriginal;

	public SparqlConstructTransformer(Query query, boolean includeOriginal) {
		this.query = query;
		this.includeOriginal = includeOriginal;
	}

	@Override
	public Model apply(Model linkedDataModel) {
		try (QueryExecution qexec = QueryExecutionFactory.create(query, linkedDataModel)) {
			Model resultModel = qexec.execConstruct();
			if (includeOriginal) {
				resultModel.add(linkedDataModel);
			}
			return resultModel;
		}
	}
}
