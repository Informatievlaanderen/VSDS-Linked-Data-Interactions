package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class LdioVirtuosoOut implements LdiOutput {
	public static final String NAME = "Ldio:VirtuosoOut";
	private static final Logger log = LoggerFactory.getLogger(LdioVirtuosoOut.class);

	private final RequestExecutor requestExecutor;
	private final String endpoint;
	private final String graph;
	private final LdiRdfWriter ldiRdfWriter;

	public LdioVirtuosoOut(RequestExecutor requestExecutor, String endpoint, String graph) {
		this.requestExecutor = requestExecutor;
		this.endpoint = endpoint;
		this.graph = graph;
		this.ldiRdfWriter = LdiRdfWriter.getRdfWriter(new LdiRdfWriterProperties().withLang(Lang.NQUADS));
	}

	@Override
	public void accept(Model linkedDataModel) {
		if (!linkedDataModel.isEmpty()) {
			final ByteArrayOutputStream output = new ByteArrayOutputStream();
			ldiRdfWriter.writeToOutputStream(linkedDataModel, output);
			String modelAsNQuads = RDFWriter.source(linkedDataModel).lang(Lang.NQUADS).asString();
			String body = "INSERT INTO <%s> { %s }".formatted(graph, modelAsNQuads);

			final PostRequest request = new PostRequest(endpoint, new RequestHeaders(List.of(
					new RequestHeader(HttpHeaders.CONTENT_TYPE, "application/sparql-update"),
					new RequestHeader(HttpHeaders.ACCEPT, "application/json"))), body);
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
}
