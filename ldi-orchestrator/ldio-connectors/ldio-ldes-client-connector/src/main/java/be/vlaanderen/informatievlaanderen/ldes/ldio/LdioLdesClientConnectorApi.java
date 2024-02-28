package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TokenService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TransferService;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.STARTING;

public class LdioLdesClientConnectorApi {
	private final TransferService transferService;
	private final TokenService tokenService;
	private final LdioLdesClient ldesClient;

	@SuppressWarnings("java:S107")
	public LdioLdesClientConnectorApi(TransferService transferService, TokenService tokenService, LdioLdesClient ldesClient) {
		this.transferService = transferService;
		this.tokenService = tokenService;
		this.ldesClient = ldesClient;
	}

	public void handleToken(String token) {
		tokenService.updateToken(token);
	}

	public String handleTransfer(String transfer) {
		String transer = transferService.startTransfer(transfer).getBodyAsString()
				.orElse("");
		ldesClient.updateStatus(STARTING);
		return transer;
	}
}
