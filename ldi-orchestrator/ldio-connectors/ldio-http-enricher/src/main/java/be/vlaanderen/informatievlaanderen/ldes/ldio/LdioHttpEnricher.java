package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.Collection;
import java.util.List;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

public class LdioHttpEnricher implements LdiTransformer {

	public static final String PAYLOAD = "<http://example.com/payload>";

	private final LdiAdapter adapter;
	private final RequestExecutor requestExecutor;
	private final RequestPropertyPaths requestPropertyPaths;

	public LdioHttpEnricher(LdiAdapter adapter, RequestExecutor requestExecutor,
			RequestPropertyPaths requestPropertyPaths) {
		this.adapter = adapter;
		this.requestExecutor = requestExecutor;
		this.requestPropertyPaths = requestPropertyPaths;
	}

	@Override
	public Collection<Model> apply(Model model) {
		final Request request = createRequest(model);
		final Response response = requestExecutor.execute(request);
		addResponseToModel(model, response);
		return List.of(model);
	}

	private void addResponseToModel(Model model, Response response) {
		final Resource subject = createResource(PAYLOAD);
		final Property predicate = createProperty(requestPropertyPaths.payloadPropertyPath());
		final String object = response.getBody().orElse("");
		model.add(subject, predicate, object);
	}

	private Request createRequest(Model model) {
		final String url = extractUrl(model);
		final RequestHeaders requestHeaders = extractRequestHeaders(model);
		return new Request(url, requestHeaders);
	}

	private String extractUrl(Model model) {
		return PropertyPathExtractor.from(requestPropertyPaths.urlPropertyPath())
				.getProperties(model)
				.stream()
				.findFirst()
				.map(RDFNode::toString)
				.orElseThrow(() -> new IllegalArgumentException("No url found on the defined property path."));
	}

	private RequestHeaders extractRequestHeaders(Model model) {
		List<RequestHeader> headers = PropertyPathExtractor.from(requestPropertyPaths.headerPropertyPath())
				.getProperties(model)
				.stream()
				.map(RDFNode::toString)
				.map(RequestHeader::from)
				.toList();

		return new RequestHeaders(headers);
	}

	private void addResponseToModelAlt(Model model, Response response) {
		List<Model> payloadModels = response
				.getBody()
				.stream()
				.flatMap(body -> adapter.apply(toContent(body, response)))
				.toList();

		// TODO TVB: 16/10/23 figure out how to map this to the model? => provide
		// subject?
	}

	private LdiAdapter.Content toContent(String body, Response response) {
		String mimeType = response
				.getFirstHeaderValue(HttpHeaders.CONTENT_TYPE)
				.orElse(ContentType.TEXT_PLAIN.getMimeType());
		return LdiAdapter.Content.of(body, mimeType);
	}

}
