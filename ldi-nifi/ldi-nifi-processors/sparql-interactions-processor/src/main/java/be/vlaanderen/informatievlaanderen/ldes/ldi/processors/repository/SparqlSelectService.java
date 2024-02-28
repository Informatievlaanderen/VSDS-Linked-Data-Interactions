package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.repository;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

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
			RDFNode node = querySolution.get(key);
			if (!node.isLiteral()) {
				jsonObject.addProperty(key, node.toString());
				continue;
			}
			Literal literal = node.asLiteral();
			RDFDatatype literalDatatype = literal.getDatatype();
			switch (literalDatatype.getURI()) {
				case "http://www.w3.org/2001/XMLSchema#integer" ->
						jsonObject.addProperty(key, (int) literal.getValue());
				case "http://www.w3.org/2001/XMLSchema#double" ->
						jsonObject.addProperty(key, (double) literal.getValue());
				case "http://www.w3.org/2001/XMLSchema#boolean" ->
						jsonObject.addProperty(key, (boolean) literal.getValue());
				default -> jsonObject.addProperty(key, literal.getValue().toString());
			}
		}
		return jsonObject;
	}
}
