package ldes.client.requestexecutor;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.function.Function;

/**
 * Handles sending the actual HTTP request to the server.
 */
public class RequestExecutor implements Function<Request, Model> {

    @Override
    public Model apply(Request request) {
        // TODO: 1/03/2023 impl
        return ModelFactory.createDefaultModel();
    }

}
