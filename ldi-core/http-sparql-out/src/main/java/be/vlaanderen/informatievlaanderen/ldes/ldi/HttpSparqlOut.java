package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.skolemisation.Skolemizer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.SparqlQuery;
import org.apache.http.HttpHeaders;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HttpSparqlOut {
	private static final Logger log = LoggerFactory.getLogger(HttpSparqlOut.class);
	private final String endpoint;
	private final SparqlQuery sparqlQuery;
	private final Skolemizer skolemizer;
	private final RequestExecutor requestExecutor;

	public HttpSparqlOut(String endpoint, SparqlQuery sparqlQuery, Skolemizer skolemizer, RequestExecutor requestExecutor) {
		this.endpoint = endpoint;
		this.sparqlQuery = sparqlQuery;
		this.skolemizer = skolemizer;
		this.requestExecutor = requestExecutor;
	}

	public void write(Model model) {
		if(model.isEmpty()) {
			return;
		}

		String query = sparqlQuery.createQuery(skolemizer.skolemize(model));

		final PostRequest request = new PostRequest(endpoint, new RequestHeaders(List.of(
				new RequestHeader(HttpHeaders.CONTENT_TYPE, "application/sparql-update"),
				new RequestHeader(HttpHeaders.ACCEPT, "application/json"))), query);
		synchronized (requestExecutor) {
			Response response = requestExecutor.execute(request);
			if (response.isSuccess()) {
				log.debug("{} {} {}", request.getMethod(), request.getUrl(), response.getHttpStatus());
			} else {
				log.atError().log("Failed to post model. The request url was {}. " +
								"The http response obtained from the server has code {} and body \"{}\".",
						response.getRequestedUrl(), response.getHttpStatus(), response.getBodyAsString().orElse(null));
			}
		}
	}
}
