package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.StringWriter;

import static java.util.Optional.ofNullable;
import static org.apache.jena.riot.Lang.TURTLE;
import static org.apache.jena.riot.RDFLanguages.nameToLang;

public class LdioHttpOut implements LdiOutput {
	private final RestTemplate restTemplate;
	private final HttpHeaders headers;
	private final String targetURL;
	private final Lang outputLanguage;

	public LdioHttpOut(RestTemplate restTemplate, HttpHeaders headers, Lang outputLanguage, String targetURL) {
		this.restTemplate = restTemplate;
		this.headers = headers;
		this.outputLanguage = outputLanguage;
		this.targetURL = targetURL;
	}

	@Override
	public void sendLinkedData(Model linkedDataModel) {
		if (!linkedDataModel.isEmpty()) {
			String content = toString(linkedDataModel, outputLanguage);

			HttpEntity<String> request = new HttpEntity<>(content, headers);
			restTemplate.postForObject(targetURL, request, String.class);
		}
	}

	public static Lang getLang(MediaType contentType) {
		if (contentType.equals(MediaType.TEXT_HTML))
			return TURTLE;
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
