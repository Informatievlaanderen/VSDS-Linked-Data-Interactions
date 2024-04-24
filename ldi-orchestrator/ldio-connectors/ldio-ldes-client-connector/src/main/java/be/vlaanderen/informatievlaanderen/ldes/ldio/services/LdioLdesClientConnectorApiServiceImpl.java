package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientConnectorApi;
import be.vlaanderen.informatievlaanderen.ldes.ldio.event.LdesClientConnectorApiCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.PipelineStatusManager;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.Map;

public class LdioLdesClientConnectorApiServiceImpl implements LdioLdesClientConnectorApiService {
	private final Map<String, LdioLdesClientConnectorApi> ldioLdesClientConnectorApis = new HashMap<>();
	private final Map<String, PipelineStatusManager> pipelineStatusManagers = new HashMap<>();

	public LdioLdesClientConnectorApiServiceImpl() {

	}

	@EventListener
	void handleNewPipelines(LdesClientConnectorApiCreatedEvent connectorApiCreatedEvent) {
		ldioLdesClientConnectorApis.put(connectorApiCreatedEvent.pipelineName(), connectorApiCreatedEvent.ldesClientConnectorApi());
	}

	@EventListener
	void handlePipelineCreatedEvent(PipelineCreatedEvent event) {
		pipelineStatusManagers.put(event.pipelineName(), event.pipelineStatusManager());
	}

	@EventListener
	void deletePipeline(PipelineDeletedEvent deletedEvent) {
		final var ldioLdesClientConnectorApi = ldioLdesClientConnectorApis.remove(deletedEvent.pipelineId());
		if (ldioLdesClientConnectorApi != null) {
			ldioLdesClientConnectorApi.shutdown();
		}
		pipelineStatusManagers.remove(deletedEvent.pipelineId());
	}
}
