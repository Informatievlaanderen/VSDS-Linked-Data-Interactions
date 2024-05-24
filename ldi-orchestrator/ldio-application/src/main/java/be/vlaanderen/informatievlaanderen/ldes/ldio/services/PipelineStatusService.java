package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineStatusEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.PipelineDoesNotExistException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.PipelineStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.StoppedPipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.StatusChangeSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.StatusChangeSource.AUTO;

@Component
public class PipelineStatusService {
	private final ApplicationEventPublisher eventPublisher;
	private final Logger logger = LoggerFactory.getLogger(PipelineStatusService.class);
	private final Map<String, SavedPipeline> savedPipelines;
	private final Map<String, PipelineStatusManager> pipelineStatusManagers;

	public PipelineStatusService(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
		this.savedPipelines = new HashMap<>();
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

	public PipelineStatus.Value updatePipelineStatus(String pipelineName, PipelineStatus pipelineStatus) {
		return Optional.of(pipelineStatusManagers.get(pipelineName))
				.map(pipelineStatusManager -> pipelineStatusManager.updatePipelineStatus(pipelineStatus))
				.orElseThrow(() -> new PipelineDoesNotExistException(pipelineName));
	}

	@EventListener
	public void handlePipelineStatusResponse(PipelineStatusEvent statusEvent) {
		SavedPipeline currentSavedPipeline = savedPipelines.get(statusEvent.pipelineId());
		if (currentSavedPipeline == null) {
			logger.warn("Non initialized pipeline received status update: {}", statusEvent.pipelineId());
			return;
		}
//		currentSavedPipeline.updateStatus(statusEvent.status(), statusEvent.statusChangeSource());
	}

	@EventListener
	public void handlePipelineCreated(PipelineCreatedEvent event) {
		pipelineStatusManagers.put(event.pipelineName(), event.pipelineStatusManager());
	}

	public PipelineStatus.Value stopPipeline(String pipelineId) {
		PipelineStatus.Value newPipelineStatus = pipelineStatusManagers.get(pipelineId).updatePipelineStatus(new StoppedPipelineStatus());
		this.savedPipelines.remove(pipelineId);
		eventPublisher.publishEvent(new PipelineDeletedEvent(pipelineId));
		return newPipelineStatus;
	}

	public Map<String, PipelineStatus.Value> getPipelineStatusOverview() {
		return pipelineStatusManagers.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getPipelineStatusValue()));
	}

	static class SavedPipeline {
		private final LdioInput ldioInput;
		private PipelineStatus status;
		private StatusChangeSource lastStatusChangeSource;

		public SavedPipeline(LdioInput ldioInput, PipelineStatus status) {
			this.ldioInput = ldioInput;
			this.status = status;
			lastStatusChangeSource = AUTO;
		}

		public void updateStatus(PipelineStatus status, StatusChangeSource statusChangeSource) {
			this.status = status;
			this.lastStatusChangeSource = statusChangeSource;
		}

	}
}
