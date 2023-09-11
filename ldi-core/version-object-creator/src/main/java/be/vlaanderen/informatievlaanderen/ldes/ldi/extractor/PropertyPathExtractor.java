package be.vlaanderen.informatievlaanderen.ldes.ldi.extractor;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PropertyPathExtractor implements PropertyExtractor {

	private final String queryString;

	private PropertyPathExtractor(String propertyPath) {
		queryString = "SELECT * where { ?subject %s ?object }".formatted(propertyPath);
	}

	/**
	 * This factory method was provided for backwards compatibility.
	 * In the past we supported properties to be provided as strings in a non IRI
	 * format (not wrapped by <>).
	 * When a property is provided as a plain string, we wrap it to an IRI.
	 * NOTE: Does not work with property paths -> ex:foo/ex:bar must always be
	 * <ex:foo>/<ex:bar>. Property paths
	 * were not supported in previous versions and there cannot be any config with
	 * property paths without <>
	 */
	public static PropertyPathExtractor from(String propertyPath) {
		return propertyPath.startsWith("<")
				? new PropertyPathExtractor(propertyPath)
				: new PropertyPathExtractor("<%s>".formatted(propertyPath));
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
