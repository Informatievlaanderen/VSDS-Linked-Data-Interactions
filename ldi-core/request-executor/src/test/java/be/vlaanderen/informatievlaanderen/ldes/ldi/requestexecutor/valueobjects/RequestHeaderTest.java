package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestHeaderTest {

    @Test
    void test_from() {
        RequestHeader result = RequestHeader.from("Content-Type: application/json");

        assertEquals("Content-Type", result.getKey());
        assertEquals("application/json", result.getValue());
    }

}