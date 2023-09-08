package be.vlaanderen.informatievlaanderen.ldes.ldi.extractor;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PropertyPathExtractor implements PropertyExtractor {

	private final String queryString;

	public PropertyPathExtractor(String propertyPath) {
		queryString = "SELECT * where { ?subject %s ?object }".formatted(propertyPath);
	}

	@Override
	public List<RDFNode> getProperties(Model model) {
		final Query query = QueryFactory.create(queryString);
		try (QueryExecution queryExecution = QueryExecutionFactory.create(query, model)) {
			ResultSet resultSet = queryExecution.execSelect();

			List<RDFNode> results = new ArrayList<>();
			while (resultSet.hasNext()) {
				results.add(resultSet.next().get("object"));
			}
			return results;
		}
	}

}
