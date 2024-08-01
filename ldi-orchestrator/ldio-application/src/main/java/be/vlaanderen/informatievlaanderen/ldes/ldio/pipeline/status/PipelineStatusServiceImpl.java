package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.events.InputCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.events.PipelineDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.PipelineDoesNotExistException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.events.PipelineStatusEvent;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.PipelineConfig.PIPELINE_NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioObserver.LDIO_DATA_IN;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.model.LdioSender.LDIO_DATA_OUT;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.PipelineStatus.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.PipelineStatusTrigger.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.StatusChangeSource.AUTO;

@Component
public class PipelineStatusServiceImpl implements PipelineStatusService {
	private final ApplicationEventPublisher eventPublisher;
	private final Logger logger = LoggerFactory.getLogger(PipelineStatusServiceImpl.class);
	private final Map<String, SavedPipeline> savedPipelines;

	public PipelineStatusServiceImpl(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
		this.savedPipelines = new HashMap<>();
	}

	@Override
	public PipelineStatus getPipelineStatus(String pipelineName) {
		return Optional.ofNullable(savedPipelines.get(pipelineName))
				.map(SavedPipeline::getStatus)
				.orElseThrow(() -> new PipelineDoesNotExistException(pipelineName));
	}

	@Override
	public StatusChangeSource getPipelineStatusChangeSource(String pipelineName) {
		return Optional.ofNullable(savedPipelines.get(pipelineName))
				.map(SavedPipeline::getLastStatusChangeSource)
				.orElseThrow(() -> new PipelineDoesNotExistException(pipelineName));
	}

	@EventListener
	public void handlePipelineStatusResponse(PipelineStatusEvent statusEvent) {
		SavedPipeline currentSavedPipeline = savedPipelines.get(statusEvent.pipelineId());
		if (currentSavedPipeline == null) {
			logger.warn("Non initialized pipeline received status update: {}", statusEvent.pipelineId());
			return;
		}
		currentSavedPipeline.updateStatus(statusEvent.status(), statusEvent.statusChangeSource());
	}

	@EventListener
	public void handlePipelineCreated(InputCreatedEvent event) {
		savedPipelines.put(event.pipelineName(), new SavedPipeline(event.ldioInput(), event.ldioInput().getStatus()));
	}

	@Override
	public PipelineStatus resumeHaltedPipeline(String pipelineId) {
		var pipelineStatus = getPipelineStatus(pipelineId);

		return switch (pipelineStatus) {
			case HALTED -> savedPipelines.get(pipelineId).getLdioInput().updateStatus(RESUME);
			case INIT -> INIT;
			case RUNNING -> RUNNING;
			case STOPPED -> STOPPED;
		};
	}

	@Override
	public PipelineStatus haltRunningPipeline(String pipelineId) {
		PipelineStatus pipelineStatus = getPipelineStatus(pipelineId);

		return switch (pipelineStatus) {
			case RUNNING -> savedPipelines.get(pipelineId).getLdioInput().updateStatus(HALT);
			case INIT -> INIT;
			case HALTED -> HALTED;
			case STOPPED -> STOPPED;
		};
	}

	@Override
	public PipelineStatus stopPipeline(String pipelineId) {
		PipelineStatus newPipelineStatus = savedPipelines.get(pipelineId).getLdioInput().updateStatus(STOP);
		while (!pipelineEmpty(pipelineId)) {
			try {
				wait(100);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		this.savedPipelines.remove(pipelineId);
		eventPublisher.publishEvent(new PipelineDeletedEvent(pipelineId));
		return newPipelineStatus;
	}

	@Override
	public Map<String, PipelineStatus> getPipelineStatusOverview() {
		return savedPipelines.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getStatus()));
	}

	private boolean pipelineEmpty(String pipelineName) {
		var in = Metrics.counter(LDIO_DATA_IN, PIPELINE_NAME, pipelineName).count();
		var out = Metrics.counter(LDIO_DATA_OUT, PIPELINE_NAME, pipelineName).count();

		return in == out;
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

		public PipelineStatus getStatus() {
			return status;
		}

		public LdioInput getLdioInput() {
			return ldioInput;
		}

		public StatusChangeSource getLastStatusChangeSource() {
			return lastStatusChangeSource;
		}
	}
}
