package ldes.client.endpointrequester.endpointresponse;

import org.apache.jena.rdf.model.Model;

/**
 * Contains the model obtained from the endPoint.
 */
public record EndpointResponse(Model model) {}
