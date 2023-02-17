package be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpointresponse;

import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import java.io.InputStream;

public class EndpointResponseFactory {

	public EndpointResponse createResponse(InputStream inputStream, Lang lang) {
		return new EndpointResponse(
				RDFParser
						.source(inputStream)
						.lang(lang)
						.build()
						.toModel());
	}

	public EndpointResponse createEmptyResponse() {
		return new EndpointResponse(ModelFactory.createDefaultModel());
	}

}
