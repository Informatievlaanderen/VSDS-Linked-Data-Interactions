package be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester;

import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpoint.Endpoint;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpointresponse.EndpointResponse;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpointresponse.repository.EndpointResponseRepository;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.startingnode.StartingNode;
import be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.startingnode.StartingNodeSupplier;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EndpointRequesterTest {

	@InjectMocks
	private EndpointRequester endpointRequester;

	@Mock
	private EndpointResponseRepository endpointResponseRepository;

	@Mock
	private StartingNodeSupplier startingNodeSupplier;

	private Model model;
	private EndpointResponse endpointResponse;

	@BeforeEach
	void setUp() {
		model = ModelFactory.createDefaultModel();
		endpointResponse = new EndpointResponse(model);
	}

	@Test
	void shouldReturnStartingNode_whenEndpointIsProvided() {
		Endpoint endpoint = new Endpoint("url", Lang.TURTLE);
		when(endpointResponseRepository.getEndpointResponse(endpoint)).thenReturn(endpointResponse);
		when(startingNodeSupplier.getStartingNode(model)).thenReturn(Optional.of(new StartingNode("start-url")));

		Optional<StartingNode> startingTreeNode = endpointRequester.determineStartingNode(endpoint);

		assertTrue(startingTreeNode.isPresent());
		assertEquals("start-url", startingTreeNode.get().url());
	}
}