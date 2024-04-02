package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.valueobjects.EdcToken;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static org.apache.commons.lang3.ThreadUtils.sleep;

public class MemoryTokenService implements TokenService {

	private final TransferService transferService;
	private final Logger log = LoggerFactory.getLogger(MemoryTokenService.class);

	private EdcToken token;

	private final MemoryTokenServiceLifecycle lifecycle = new MemoryTokenServiceLifecycle();

	public MemoryTokenService(TransferService transferService) {
		this.transferService = transferService;
	}

	public RequestHeader waitForTokenHeader() {
		lifecycle.checkPipelineState();

		if (token != null) {
			return token.getTokenHeader();
		}

		try {
			log.info("waiting for token..");
			sleep(Duration.ofSeconds(5));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		return waitForTokenHeader();
	}

	public void invalidateToken() {
		token = null;
		transferService.refreshTransfer();
	}

	@Override
	public void updateToken(String token) {
		this.token = EdcToken.fromJsonString(token);
	}

	public void pause() {
		lifecycle.pause();
	}

	@Override
	public void resume() {
		lifecycle.resume();
	}

	@Override
	public void shutdown() {
		lifecycle.shutdown();
	}

}
