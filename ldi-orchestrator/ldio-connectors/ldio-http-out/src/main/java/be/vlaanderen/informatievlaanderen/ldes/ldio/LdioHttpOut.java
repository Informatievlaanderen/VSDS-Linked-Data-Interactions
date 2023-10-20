package be.vlaanderen.informatievlaanderen.ldes.ldio;

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
	private final Lang outputLanguage;

	public LdioHttpOut(RequestExecutor requestExecutor, Lang outputLanguage, String targetURL) {
		this.requestExecutor = requestExecutor;
		this.outputLanguage = outputLanguage;
		this.targetURL = targetURL;
	}

	@Override
	public void accept(Model linkedDataModel) {
		if (!linkedDataModel.isEmpty()) {
			final String content = toString(linkedDataModel, outputLanguage);
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

	public static String toString(final Model model, final Lang lang) {
		StringWriter stringWriter = new StringWriter();
		RDFDataMgr.write(stringWriter, model, lang);
		return stringWriter.toString();
	}
}
