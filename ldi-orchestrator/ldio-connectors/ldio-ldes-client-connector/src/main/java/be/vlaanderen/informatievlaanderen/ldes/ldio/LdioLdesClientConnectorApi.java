package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TokenService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TransferService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.StartedPipelineStatus;
import io.micrometer.observation.ObservationRegistry;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import org.springframework.context.ApplicationEventPublisher;

public class LdioLdesClientConnectorApi extends LdioLdesClient {
	private final TransferService transferService;
	private final TokenService tokenService;

	public LdioLdesClientConnectorApi(ComponentExecutor componentExecutor,
									  String pipelineName,
									  ObservationRegistry observationRegistry,
									  MemberSupplier memberSupplier,
									  ApplicationEventPublisher applicationEventPublisher,
									  TransferService transferService,
									  TokenService tokenService) {
		super(componentExecutor, pipelineName, observationRegistry, memberSupplier, applicationEventPublisher);
		this.transferService = transferService;
		this.tokenService = tokenService;
	}

	public void handleToken(String token) {
		tokenService.updateToken(token);
	}

	public String handleTransfer(String transfer) {
		String response = transferService.startTransfer(transfer).getBodyAsString().orElse("");
		updateStatus(new StartedPipelineStatus());
		return response;
	}

	@Override
	public void shutdown() {
		super.shutdown();
		tokenService.shutdown();
	}

	@Override
	public synchronized void resume() {
		super.resume();
		tokenService.resume();
	}

	@Override
	public void pause() {
		super.shutdown();
		tokenService.pause();
	}

}
