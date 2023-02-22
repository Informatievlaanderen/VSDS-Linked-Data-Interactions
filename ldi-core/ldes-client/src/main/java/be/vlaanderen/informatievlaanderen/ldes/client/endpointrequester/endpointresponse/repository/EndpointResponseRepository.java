package be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpointresponse.repository;

import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpoint.Endpoint;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpointresponse.EndpointResponse;

public interface EndpointResponseRepository {

	EndpointResponse getEndpointResponse(Endpoint endpoint);

}
