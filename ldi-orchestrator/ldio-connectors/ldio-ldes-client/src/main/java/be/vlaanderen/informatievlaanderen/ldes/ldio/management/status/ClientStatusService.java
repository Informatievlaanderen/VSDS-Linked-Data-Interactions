package be.vlaanderen.informatievlaanderen.ldes.ldio.management.status;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.events.PipelineDeletedEvent;
import ldes.client.treenodesupplier.domain.valueobject.ClientStatus;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ClientStatusService {
	private final Map<String, ClientStatus> clientStatuses;

	public ClientStatusService() {
		this.clientStatuses = new HashMap<>();
	}

	public void updateStatus(String pipelineId, ClientStatus status) {
		clientStatuses.put(pipelineId, status);
	}

	public List<ClientStatusTo> getClientStatuses() {
		return clientStatuses.entrySet()
				.stream()
				.map(entry -> new ClientStatusTo(entry.getKey(), entry.getValue()))
				.toList();
	}

	public Optional<ClientStatus> getClientStatus(String pipelineId) {
		return Optional.ofNullable(clientStatuses.get(pipelineId));
	}

	@EventListener
	public void pipelineDeletedEventHandler(PipelineDeletedEvent event) {
		clientStatuses.remove(event.pipelineId());
	}
}
