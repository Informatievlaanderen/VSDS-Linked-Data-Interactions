package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EdcTokenTest {


    @Nested
    class GetTokenHeader {

        @Test
        void shouldThrowException_WhenAuthKeyIsMissing() {
            EdcToken edcToken = EdcToken.fromJsonString("{'authCode': 'auth-code'}");

            var illegalArgumentException = assertThrows(IllegalArgumentException.class, edcToken::getTokenHeader);
            assertTrue(illegalArgumentException.getMessage().contains("authKey not found"));
        }

        @Test
        void shouldThrowException_WhenAuthCodeIsMissing() {
            EdcToken edcToken = EdcToken.fromJsonString("{'authKey': 'auth-key'}");

            var illegalArgumentException = assertThrows(IllegalArgumentException.class, edcToken::getTokenHeader);
            assertTrue(illegalArgumentException.getMessage().contains("authCode not found"));
        }

        @Test
        void shouldReturnToken_WhenPresent() {
            EdcToken edcToken = EdcToken.fromJsonString("{'authKey': 'auth-key', 'authCode': 'auth-code'}");

            RequestHeader tokenHeader = edcToken.getTokenHeader();

            assertEquals("auth-key", tokenHeader.getKey());
            assertEquals("auth-code", tokenHeader.getValue());
        }
    }

}