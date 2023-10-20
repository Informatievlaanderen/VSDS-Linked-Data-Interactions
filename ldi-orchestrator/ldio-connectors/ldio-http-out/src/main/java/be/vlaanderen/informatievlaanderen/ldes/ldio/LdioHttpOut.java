package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.springframework.http.HttpEntity;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.StringWriter;
import java.util.List;
import static java.util.Optional.ofNullable;
import static org.apache.jena.riot.RDFLanguages.nameToLang;

public class LdioHttpOut implements LdiOutput {

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
			String content = LdiRdfWriter.getRdfWriter(rdfWriterProperties).write(linkedDataModel);
			final String contentType = outputLanguage.getHeaderString();
			final RequestHeader requestHeader = new RequestHeader(HttpHeaders.CONTENT_TYPE, contentType);
			final PostRequest request = new PostRequest(targetURL, new RequestHeaders(List.of(requestHeader)), content);
			requestExecutor.execute(request);
		}
	}

	public static Lang getLang(MediaType contentType) {
		return ofNullable(nameToLang(contentType.getType() + "/" + contentType.getSubtype()))
				.orElseGet(() -> ofNullable(nameToLang(contentType.getSubtype()))
						.orElseThrow());
	}
}
