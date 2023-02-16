package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;

public class SparqlConstructTransformer implements LdiTransformer {
	private final Query query;
	private final boolean inferMode;

	public SparqlConstructTransformer(Query query, boolean inferMode) {
		this.query = query;
		this.inferMode = inferMode;
	}

	public Model execute(Model linkedDataModel) {
		try (QueryExecution qexec = QueryExecutionFactory.create(query, linkedDataModel)) {
			Model resultModel = qexec.execConstruct();
			if (inferMode) {
				resultModel.add(linkedDataModel);
			}
			return resultModel;
		}
	}
}
