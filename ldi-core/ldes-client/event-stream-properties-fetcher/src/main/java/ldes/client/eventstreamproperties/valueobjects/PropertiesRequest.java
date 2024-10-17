package ldes.client.eventstreamproperties.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import org.apache.http.HttpHeaders;
import org.apache.jena.riot.Lang;

import java.util.List;

public record PropertiesRequest(String url, Lang lang) {

	public Request createRequest() {
		RequestHeaders requestHeaders = new RequestHeaders(List.of(
				new RequestHeader(HttpHeaders.ACCEPT, lang.getHeaderString())
		));
		return new GetRequest(url, requestHeaders);
	}

	public PropertiesRequest withUrl(String url) {
		return new PropertiesRequest(url, lang);
	}
}
