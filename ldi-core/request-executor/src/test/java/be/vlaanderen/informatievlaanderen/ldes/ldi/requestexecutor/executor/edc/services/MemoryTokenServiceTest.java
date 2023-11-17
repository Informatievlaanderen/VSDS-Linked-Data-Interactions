package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemoryTokenServiceTest {

	@Mock
	private TransferService transferService;

	@InjectMocks
	private MemoryTokenService memoryTokenService;

	@Test
	void test_memoryTokenService() {
		memoryTokenService.updateToken("{'authKey': 'auth-key', 'authCode': 'auth-code'}");
		// token is found after update
		RequestHeader requestHeader = memoryTokenService.waitForTokenHeader();
		assertNotNull(requestHeader);

		verify(transferService, times(0)).refreshTransfer();
		memoryTokenService.invalidateToken();

		// no token is found after invalidation
		final var duration = Duration.ofSeconds(1);
		assertThrows(Error.class,
				() -> assertTimeoutPreemptively(duration, () -> memoryTokenService.waitForTokenHeader()));
		verify(transferService, times(1)).refreshTransfer();
	}

}