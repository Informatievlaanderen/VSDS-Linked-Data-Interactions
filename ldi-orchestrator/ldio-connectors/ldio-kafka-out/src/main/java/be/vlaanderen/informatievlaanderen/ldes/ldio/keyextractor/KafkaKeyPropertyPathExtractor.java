package be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathLib;
import org.apache.jena.sparql.path.PathParser;


public class KafkaKeyPropertyPathExtractor implements KafkaKeyExtractor {

    public static final String PATH_FOR_KAFKA_KEY = "https://path-for-kafka-key";

    private final String queryString = "SELECT * where { ?s <%s> ?o }".formatted(PATH_FOR_KAFKA_KEY);

    private KafkaKeyPropertyPathExtractor() {
    }

    // TODO: 5/04/2023 jsut put in propertyPath in query?
    public static KafkaKeyPropertyPathExtractor from(String propertyPath) {
        final Path path = PathParser.parse(propertyPath, PrefixMapping.Standard) ;
        PathLib.install(PATH_FOR_KAFKA_KEY, path);
        return new KafkaKeyPropertyPathExtractor();
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
