package ldes.client.eventstreamproperties;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import ldes.client.eventstreamproperties.services.StartingNodeSpecificationFactory;
import ldes.client.eventstreamproperties.valueobjects.EventStreamProperties;
import ldes.client.eventstreamproperties.valueobjects.PropertiesRequest;
import ldes.client.eventstreamproperties.valueobjects.StartingNodeSpecification;
import org.apache.jena.riot.RDFParser;

import java.io.ByteArrayInputStream;

public class EventStreamPropertiesFetcher {
	private final RequestExecutor requestExecutor;

	public EventStreamPropertiesFetcher(RequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
	}

	public EventStreamProperties fetchEventStreamProperties(PropertiesRequest request) {
		final EventStreamProperties eventStreamProperties = executePropertiesRequest(request);

		if(eventStreamProperties.containsRequiredProperties()) {
			return eventStreamProperties;
		}

		return executePropertiesRequest(request.withUrl(eventStreamProperties.getUri()));

	}

	private EventStreamProperties executePropertiesRequest(PropertiesRequest request) {
		final Response response = requestExecutor.execute(request.createRequest());

		if(response.isOk()) {
			return response.getBody()
					.map(ByteArrayInputStream::new)
					.map(body -> RDFParser.source(body).lang(request.lang()).toModel())
					.map(StartingNodeSpecificationFactory::fromModel)
					.map(StartingNodeSpecification::extractEventStreamProperties)
					.orElseThrow();
		}

		if(response.isRedirect()) {
			return response.getRedirectLocation()
					.map(request::withUrl)
					.map(this::executePropertiesRequest)
					.orElseThrow(() -> new IllegalStateException("No Location Header in redirect."));
		}

		throw new UnsupportedOperationException(
				"Cannot handle response " + response.getHttpStatus() + " of EventStreamPropertiesRequest " + request);
	}


}
