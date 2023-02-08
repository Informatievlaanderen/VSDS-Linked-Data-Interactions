package be.vlaanderen.informatievlaanderen.ldes.ldto.output;

import be.vlaanderen.informatievlaanderen.ldes.ldto.config.OrchestratorConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldto.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldto.types.LdtoOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.ldto.LdtoConstants.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldto.converter.RdfModelConverter.getLang;

public class LdtoHttpOut implements LdtoOutput {
	private RestTemplate restTemplate;
	private HttpHeaders headers;
	private String targetURL;
	private Lang outputLanguage = DEFAULT_OUTPUT_LANG;


	public LdtoHttpOut(Map<String, String> config) {
		init(config);
	}

	@Override
	public void init(Map<String, String> config) {
		this.restTemplate = new RestTemplate();
		this.headers = new HttpHeaders();

		if (config.containsKey(CONTENT_TYPE)) {
			outputLanguage = getLang(
					Objects.requireNonNull(MediaType.valueOf(config.get(CONTENT_TYPE))));
		}

		headers.setContentType(MediaType.valueOf(outputLanguage.getContentType().getContentTypeStr()));
		targetURL = Objects.requireNonNull(config.get(ENDPOINT));
	}

	@Override
	public void sendLinkedData(Model linkedDataModel) {
		if (!linkedDataModel.isEmpty()) {
			String content = RdfModelConverter.toString(linkedDataModel, outputLanguage);

			HttpEntity<String> request = new HttpEntity<>(content, headers);
			restTemplate.postForObject(targetURL, request, String.class);
		}
	}
}
