package be.vlaanderen.informatievlaanderen.ldes.ldi.repository;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;

import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SparqlSelectService {

	public JsonArray executeSelect(Model inputModel, String queryString) {
		final Query query = QueryFactory.create(queryString);
		try (QueryExecution queryExecution = QueryExecutionFactory.create(query, inputModel)) {
			ResultSet resultSet = queryExecution.execSelect();
			return toJsonArray(resultSet);
		}
	}

	private JsonArray toJsonArray(ResultSet resultSet) {
		JsonArray result = new JsonArray();
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			result.add(toJsonObject(querySolution));
		}
		return result;
	}

	private JsonObject toJsonObject(QuerySolution querySolution) {
		JsonObject jsonObject = new JsonObject();
		Iterator<String> varNames = querySolution.varNames();
		while (varNames.hasNext()) {
			final String key = varNames.next();
			jsonObject.addProperty(key, querySolution.get(key).toString());
		}
		return jsonObject;
	}

}
