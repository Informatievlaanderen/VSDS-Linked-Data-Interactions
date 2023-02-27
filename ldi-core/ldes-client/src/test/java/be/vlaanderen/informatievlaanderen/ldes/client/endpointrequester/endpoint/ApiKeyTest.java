package be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.endpoint;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiKeyTest {

    @Test
    void empty() {
        assertTrue(ApiKey.empty().key().isEmpty());
        assertTrue(ApiKey.empty().header().isEmpty());
    }

}