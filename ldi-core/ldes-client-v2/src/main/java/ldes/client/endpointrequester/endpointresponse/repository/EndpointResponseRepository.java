package ldes.client.endpointrequester.endpointresponse.repository;

import ldes.client.endpointrequester.endpoint.Endpoint;
import ldes.client.endpointrequester.endpointresponse.EndpointResponse;

public interface EndpointResponseRepository {

	EndpointResponse getEndpointResponse(Endpoint endpoint);

}
