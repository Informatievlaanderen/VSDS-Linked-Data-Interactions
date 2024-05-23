package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineDoesNotExistException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.PipelineStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.HaltedPipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.ResumedPipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.StoppedPipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.StatusChangeSource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PipelineStatusService {
	private final Map<String, PipelineStatusManager> pipelineStatusManagers;

	public PipelineStatusService() {
		this.pipelineStatusManagers = new HashMap<>();
	}

	public PipelineStatus getPipelineStatus(String pipelineName) {
		return Optional.ofNullable(pipelineStatusManagers.get(pipelineName))
				.map(PipelineStatusManager::getPipelineStatus)
				.orElseThrow(() -> new PipelineDoesNotExistException(pipelineName));
	}

	public StatusChangeSource getPipelineStatusChangeSource(String pipelineName) {
		return Optional.ofNullable(pipelineStatusManagers.get(pipelineName))
				.map(PipelineStatusManager::getLastStatusChangeSource)
				.orElseThrow(() -> new PipelineDoesNotExistException(pipelineName));
	}

	public PipelineStatus updatePipelineStatus(String pipelineName, PipelineStatus pipelineStatus) {
		return Optional.ofNullable(pipelineStatusManagers.get(pipelineName))
				.map(pipelineStatusManager -> pipelineStatusManager.updatePipelineStatus(pipelineStatus))
				.orElseThrow(() -> new PipelineDoesNotExistException(pipelineName));
	}

	public PipelineStatus.Value haltPipeline(String pipelineName) {
		return updatePipelineStatus(pipelineName, new HaltedPipelineStatus()).getStatusValue();
	}

	public PipelineStatus.Value resumePipeline(String pipelineName) {
		return updatePipelineStatus(pipelineName, new ResumedPipelineStatus()).getStatusValue();
	}

	public PipelineStatus.Value stopPipeline(String pipelineId) {
		PipelineStatus.Value newPipelineStatus = updatePipelineStatus(pipelineId, new StoppedPipelineStatus()).getStatusValue();
		this.pipelineStatusManagers.remove(pipelineId);
		return newPipelineStatus;
	}

	public Map<String, PipelineStatus.Value> getPipelineStatusOverview() {
		return pipelineStatusManagers.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getPipelineStatusValue()));
	}

	@EventListener
	public void handlePipelineCreated(PipelineCreatedEvent event) {
		pipelineStatusManagers.put(event.pipelineName(), event.pipelineStatusManager());
	}
}
