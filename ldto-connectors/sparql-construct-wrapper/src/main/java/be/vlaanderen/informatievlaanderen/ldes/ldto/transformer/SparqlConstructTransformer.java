package be.vlaanderen.informatievlaanderen.ldes.ldto.transformer;

import be.vlaanderen.informatievlaanderen.ldes.ldto.types.LdtoTransformer;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;

import java.util.Map;
import java.util.Objects;

public class SparqlConstructTransformer implements LdtoTransformer {

	private final SparqlConstructTransformerSDK sdk;

	public SparqlConstructTransformer(Map<String, String> config) {
		this.sdk = new SparqlConstructTransformerSDK(config);
	}

	public Model execute(Model linkedDataModel) {
		Model execute = sdk.execute(linkedDataModel);
		return execute;
	}
}
