package ldes.client.requestexecutor;

import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestProcessorIT {

    @Test
    void test(){
        RequestProcessor requestProcessor = new RequestProcessor();

        Response response = requestProcessor.processRequest(new Request("https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances", "application/ld+json"));

        assertEquals(200,response.getHttpStatus());
    }

}