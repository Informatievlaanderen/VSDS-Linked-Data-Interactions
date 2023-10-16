package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import com.apicatalog.rdf.Rdf;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.util.Collection;
import java.util.List;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

public class LdioHttpEnricher implements LdiTransformer {

    private final LdiAdapter adapter;
    private final RequestExecutor requestExecutor;
    private final RequestPropertyPaths requestPropertyPaths;

    public LdioHttpEnricher(LdiAdapter adapter, RequestExecutor requestExecutor, RequestPropertyPaths requestPropertyPaths) {
        this.adapter = adapter;
        this.requestExecutor = requestExecutor;
        this.requestPropertyPaths = requestPropertyPaths;
    }

    @Override
    public Collection<Model> apply(Model model) {
        String url = PropertyPathExtractor.from(requestPropertyPaths.urlPropertyPath()).getProperties(model).stream().findFirst().map(RDFNode::toString).orElseThrow();
        List<Resource> headerSubjects = PropertyPathExtractor.from(requestPropertyPaths.headerPropertyPath()).getProperties(model).stream().map(RDFNode::asResource).toList();
        List<RequestHeader> headers = headerSubjects.stream().map(subject -> new RequestHeader(
                model.listObjectsOfProperty(createProperty("http://example.com/key")).next().toString(),
                model.listObjectsOfProperty(createProperty("http://example.com/value")).next().toString()
        )).toList();
        RequestHeaders requestHeaders = new RequestHeaders(headers);


        Request request = new Request(url, requestHeaders);
        Response response = requestExecutor.execute(request);

//        List<Model> bodyModels = response
//                .getBody()
//                .stream()
//                .flatMap(body -> adapter.apply(toContent(body, response)))
//                .toList();

        model.add(createResource("<http://example.com/payload>"), createProperty(requestPropertyPaths.payloadPropertyPath()), response.getBody().orElse(""));

        // TODO TVB: 16/10/23 add response on model ('payload') before forward
        return List.of(model);
    }

    private static LdiAdapter.Content toContent(String body, Response response) {
        return LdiAdapter.Content.of(body, response.getFirstHeaderValue(HttpHeaders.CONTENT_TYPE).orElse(ContentType.TEXT_PLAIN.getMimeType()));
    }

}
