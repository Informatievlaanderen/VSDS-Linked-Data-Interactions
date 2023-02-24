package ldes.client.endpointrequester;

import ldes.client.endpointrequester.endpoint.Endpoint;
import ldes.client.endpointrequester.startingnode.StartingNode;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EndpointRequesterTest {

    @Test
    void test(){
        EndpointRequester endpointRequester = new EndpointRequester();
        Optional<StartingNode> startingNode = endpointRequester.determineStartingNode(new Endpoint("https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2021-10-20T12:05:32.563Z", Lang.JSONLD));
        System.out.println();
    }

}