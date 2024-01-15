package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.components.LdioSender;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineStatusEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.SenderCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.*;

@Component
public class SenderQueueingService {
	private final ApplicationEventPublisher applicationEventPublisher;
	private final Map<String, SenderStatus> senderStatus;

	public SenderQueueingService(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
		this.senderStatus = new HashMap<>();
	}

	public PipelineStatus getPipelineStatus(String pipelineName) {
		return Optional.ofNullable(senderStatus.get(pipelineName))
				.map(SenderStatus::getStatus)
				.orElseThrow(() -> new IllegalArgumentException("No pipeline defined for name " + pipelineName));
	}

	@EventListener
	public void handlePipelineStatusResponse(PipelineStatusEvent statusEvent) {
		SenderStatus currentSenderStatus = senderStatus.get(statusEvent.pipelineId());
		if (currentSenderStatus == null) {
			return;
		}
		currentSenderStatus.updateStatus(statusEvent.status());
	}

	@EventListener
	public void handleSenderCreated(SenderCreatedEvent event) {
		senderStatus.put(event.pipelineName(), new SenderStatus(event.ldioSender(), RUNNING));
	}

	public PipelineStatus resumeHaltedPipeline(String pipelineId) {
		PipelineStatus pipelineStatus = getPipelineStatus(pipelineId);

		return switch (pipelineStatus) {
			case RUNNING -> RUNNING;
			case HALTED -> {
				applicationEventPublisher.publishEvent(new PipelineStatusEvent(pipelineId, RESUMING));
				senderStatus.get(pipelineId).getLdioSender().updateStatus(RESUMING);
				yield RESUMING;
			}
			case RESUMING -> RESUMING;
		};


	}

	public PipelineStatus haltRunningPipeline(String pipelineId) {
		PipelineStatus pipelineStatus = getPipelineStatus(pipelineId);

		return switch (pipelineStatus) {
			case RUNNING, RESUMING -> {
				applicationEventPublisher.publishEvent(new PipelineStatusEvent(pipelineId, HALTED));
				senderStatus.get(pipelineId).getLdioSender().updateStatus(HALTED);
				yield HALTED;
			}
			case HALTED -> HALTED;
		};


	}

	public Map<String, PipelineStatus> getPipelineStatusOverview() {
		return senderStatus.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getStatus()));
	}

	static class SenderStatus {
		private final LdioSender ldioSender;
		private PipelineStatus status;

		public SenderStatus(LdioSender ldioSender, PipelineStatus status) {
			this.ldioSender = ldioSender;
			this.status = status;
		}

		public void updateStatus(PipelineStatus status) {
			this.status = status;
		}

		public PipelineStatus getStatus() {
			return status;
		}

		public LdioSender getLdioSender() {
			return ldioSender;
		}
	}
}
