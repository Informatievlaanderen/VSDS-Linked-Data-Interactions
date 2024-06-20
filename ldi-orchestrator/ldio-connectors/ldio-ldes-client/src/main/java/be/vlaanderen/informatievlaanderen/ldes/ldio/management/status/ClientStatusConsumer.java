package be.vlaanderen.informatievlaanderen.ldes.ldio.management.status;

import ldes.client.treenodesupplier.domain.valueobject.ClientStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class ClientStatusConsumer implements Consumer<ClientStatus> {
	private final Logger log = LoggerFactory.getLogger(ClientStatusConsumer.class);
	private final String pipelineId;
	private final ClientStatusService statusService;
	private ClientStatus previousState;

	public ClientStatusConsumer(String pipelineId, ClientStatusService statusService) {
		this.pipelineId = pipelineId;
		this.statusService = statusService;
	}

	@Override
	public void accept(ClientStatus clientStatus) {
		if (clientStatus != previousState) {
			log.info("LDES Client pipeline '{}' has status {}", pipelineId, clientStatus);
			statusService.updateStatus(pipelineId, clientStatus);
			previousState = clientStatus;
		}
	}
}
