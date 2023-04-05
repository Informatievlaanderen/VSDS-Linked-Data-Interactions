package be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;


public class KafkaKeyPropertyPathExtractor implements KafkaKeyExtractor {

    private final String queryString;

    public KafkaKeyPropertyPathExtractor(String propertyPath) {
        queryString = "SELECT * where { ?s %s ?o }".formatted(propertyPath);
    }

    @Override
    public String getKey(Model model) {
        final Query query = QueryFactory.create(queryString);
        try (QueryExecution queryExecution = QueryExecutionFactory.create(query, model)) {
            ResultSet resultSet = queryExecution.execSelect();
            return resultSet.hasNext() ? resultSet.next().get("o").toString() : null;
        }
    }

}
