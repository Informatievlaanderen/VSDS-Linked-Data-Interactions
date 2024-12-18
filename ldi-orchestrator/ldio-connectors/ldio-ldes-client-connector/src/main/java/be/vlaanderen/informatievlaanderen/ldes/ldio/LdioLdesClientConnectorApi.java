package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TokenService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TransferService;

public class LdioLdesClientConnectorApi {
	private final TransferService transferService;
	private final TokenService tokenService;

	public LdioLdesClientConnectorApi(TransferService transferService, TokenService tokenService) {
		this.transferService = transferService;
		this.tokenService = tokenService;
	}

	public void handleToken(String token) {
		tokenService.updateToken(token);
	}

	public String handleTransfer(String transfer) {
		return transferService.startTransfer(transfer).getBodyAsString().orElse("");
	}

	public void shutdown() {
		tokenService.shutdown();
	}

	public void resume() {
		tokenService.resume();
	}

	public void pause() {
		tokenService.pause();
	}

}
