package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.*;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

public class LdioHttpEnricher implements LdiTransformer {

	private static final Logger LOGGER = LoggerFactory.getLogger(LdioHttpEnricher.class);

	private final LdiAdapter adapter;
	private final RequestExecutor requestExecutor;
	private final RequestPropertyPathExtractors propertyPathExtractors;

	public LdioHttpEnricher(LdiAdapter adapter, RequestExecutor requestExecutor,
			RequestPropertyPathExtractors propertyPathExtractors) {
		this.adapter = adapter;
		this.requestExecutor = requestExecutor;
		this.propertyPathExtractors = propertyPathExtractors;
	}

	@Override
	public Collection<Model> apply(Model model) {
		final Request request = createRequest(model);
		final Response response = requestExecutor.execute(request);

		LOGGER.info(request.getMethod() + " " + request.getUrl() + " " + response.getHttpStatus());

		addResponseToModel(model, response);
		return List.of(model);
	}

	private Request createRequest(Model model) {
		final String url = extractUrl(model);
		final RequestHeaders requestHeaders = extractRequestHeaders(model);

		String httpMethod = extractHttpMethod(model);
		return switch (httpMethod) {
			case "GET" -> new GetRequest(url, requestHeaders);
			case "POST" -> new PostRequest(url, requestHeaders, StringEscapeUtils.unescapeJava(extractBody(model)));
			default -> throw new IllegalStateException("Http method not supported: " + httpMethod);
		};
	}

	private String extractBody(Model model) {
		return propertyPathExtractors.bodyPropertyPathExtractor()
				.getProperties(model)
				.stream()
				.findFirst()
				.map(RDFNode::toString)
				.orElse(null);
	}

	private String extractHttpMethod(Model model) {
		return propertyPathExtractors.httpMethodPropertyPathExtractor()
				.getProperties(model)
				.stream()
				.findFirst()
				.map(RDFNode::toString)
				.map(String::toUpperCase)
				.orElse("GET");
	}

	private String extractUrl(Model model) {
		return propertyPathExtractors.urlPropertyPathExtractor()
				.getProperties(model)
				.stream()
				.findFirst()
				.map(RDFNode::toString)
				.orElseThrow(() -> new IllegalArgumentException("No url found on the defined property path."));
	}

	private RequestHeaders extractRequestHeaders(Model model) {
		List<RequestHeader> headers = propertyPathExtractors.headerPropertyPathExtractor()
				.getProperties(model)
				.stream()
				.map(RDFNode::toString)
				.map(RequestHeader::from)
				.toList();

		return new RequestHeaders(headers);
	}

	private void addResponseToModel(Model model, Response response) {
		if (response.isSuccess()) {
			response
					.getBody()
					.stream()
					.flatMap(body -> adapter.apply(toContent(body, response)))
					.toList()
					.forEach(model::add);
		} else {
			LOGGER.atWarn().log("Failed to enrich model. The request url was {}. " +
					"The http response obtained from the server has code {} and body \"{}\".",
					response.getRequestedUrl(), response.getHttpStatus(), response.getBody().orElse(null));
		}
	}

	private LdiAdapter.Content toContent(String body, Response response) {
		String mimeType = response
				.getFirstHeaderValue(HttpHeaders.CONTENT_TYPE)
				.map(ContentType::parse)
				.map(ContentType::getMimeType)
				.orElse(ContentType.TEXT_PLAIN.getMimeType());
		return LdiAdapter.Content.of(body, mimeType);
	}

}
