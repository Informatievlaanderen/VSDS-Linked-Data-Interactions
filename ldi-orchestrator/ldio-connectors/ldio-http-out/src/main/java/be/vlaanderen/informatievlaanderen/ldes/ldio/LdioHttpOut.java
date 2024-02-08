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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class LdioHttpOut implements LdiOutput {
	public static final String NAME = "Ldio:HttpOut";
	private static final Logger log = LoggerFactory.getLogger(LdioHttpOut.class);

	private final RequestExecutor requestExecutor;
	private final String targetURL;
	private final LdiRdfWriterProperties rdfWriterProperties;

	public LdioHttpOut(RequestExecutor requestExecutor, String targetURL,
			LdiRdfWriterProperties rdfWriterProperties) {
		this.requestExecutor = requestExecutor;
		this.targetURL = targetURL;
		this.rdfWriterProperties = rdfWriterProperties;
	}

	@Override
	public void accept(Model linkedDataModel) {
		if (!linkedDataModel.isEmpty()) {
			final ByteArrayOutputStream output = new ByteArrayOutputStream();
			LdiRdfWriter.getRdfWriter(rdfWriterProperties).writeToOutputStream(linkedDataModel, output);
			final String contentType = rdfWriterProperties.getLang().getHeaderString();
			final RequestHeader requestHeader = new RequestHeader(HttpHeaders.CONTENT_TYPE, contentType);
			final PostRequest request = new PostRequest(targetURL, new RequestHeaders(List.of(requestHeader)), output.toByteArray());
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
