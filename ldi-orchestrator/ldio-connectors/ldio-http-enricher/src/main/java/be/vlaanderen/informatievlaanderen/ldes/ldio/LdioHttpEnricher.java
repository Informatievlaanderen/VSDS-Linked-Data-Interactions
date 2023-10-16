package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.jena.rdf.model.Model;

import java.util.Collection;
import java.util.List;

public class LdioHttpEnricher implements LdiTransformer {

    private final LdiAdapter adapter;
    private final RequestExecutor requestExecutor;

    public LdioHttpEnricher(LdiAdapter adapter, RequestExecutor requestExecutor) {
        this.adapter = adapter;
        this.requestExecutor = requestExecutor;
    }

    @Override
    public Collection<Model> apply(Model model) {
        // TODO TVB: 16/10/23 extract request details from model
        // TODO TVB: 16/10/23 type (get/post) and body and content-type?
//        ContentType.APPLICATION_JSON
        Request request = new Request(null, null);
        Response response = requestExecutor.execute(request);
        // TODO TVB: 16/10/23 add response on model ('body') before forward
        return List.of(model);
    }
}
