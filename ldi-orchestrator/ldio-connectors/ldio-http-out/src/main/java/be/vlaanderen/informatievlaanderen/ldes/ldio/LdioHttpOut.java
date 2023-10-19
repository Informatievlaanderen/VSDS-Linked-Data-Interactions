package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.RdfFormatter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import static java.util.Optional.ofNullable;
import static org.apache.jena.riot.RDFLanguages.nameToLang;

public class LdioHttpOut implements LdiOutput {
	private final RestTemplate restTemplate;
	private final HttpHeaders headers;
	private final String targetURL;
	private final Lang outputLanguage;
	private final String frameType;

	public LdioHttpOut(RestTemplate restTemplate, HttpHeaders headers, Lang outputLanguage, String targetURL,
			String frameType) {
		this.restTemplate = restTemplate;
		this.headers = headers;
		this.outputLanguage = outputLanguage;
		this.targetURL = targetURL;
		this.frameType = frameType;
	}

	@Override
	public void accept(Model linkedDataModel) {
		if (!linkedDataModel.isEmpty()) {
			String content = RdfFormatter.formatModel(linkedDataModel, outputLanguage, frameType);
			HttpEntity<String> request = new HttpEntity<>(content, headers);
			restTemplate.postForObject(targetURL, request, String.class);
		}
	}

	public static Lang getLang(MediaType contentType) {
		return ofNullable(nameToLang(contentType.getType() + "/" + contentType.getSubtype()))
				.orElseGet(() -> ofNullable(nameToLang(contentType.getSubtype()))
						.orElseThrow());
	}
}
