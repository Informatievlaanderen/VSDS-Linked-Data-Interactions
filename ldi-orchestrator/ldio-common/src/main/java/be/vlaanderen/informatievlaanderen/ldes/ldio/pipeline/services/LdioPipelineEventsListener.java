package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.events.InputCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.events.PipelineDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.events.PipelineStatusEvent;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * EventListener that listens that pipeline status changes
 *
 * @param <T> Generic type that can be any LdiComponent
 */
public class LdioPipelineEventsListener<T extends LdiComponent> {
	private final Map<String, T> ldioComponents = new HashMap<>();
	private final PipelineStatusChangedBehavior<T> startBehavior;
	private final PipelineStatusChangedBehavior<T> pauseBehavior;
	private final PipelineStatusChangedBehavior<T> resumeBehavior;
	private final PipelineStatusChangedBehavior<T> stopBehavior;

	private LdioPipelineEventsListener(PipelineStatusChangedBehavior<T> startBehavior, PipelineStatusChangedBehavior<T> pauseBehavior, PipelineStatusChangedBehavior<T> resumeBehavior, PipelineStatusChangedBehavior<T> stopBehavior) {
		this.startBehavior = startBehavior;
		this.pauseBehavior = pauseBehavior;
		this.resumeBehavior = resumeBehavior;
		this.stopBehavior = stopBehavior;
	}

	public void registerComponent(String pipelineName, T ldioComponent) {
		Objects.requireNonNull(pipelineName);
		ldioComponents.put(pipelineName, ldioComponent);
	}

	@EventListener
	public void handlePipelineDeletedEvent(PipelineDeletedEvent event) {
		T ldioComponent = ldioComponents.remove(event.pipelineId());
		if (ldioComponent != null && stopBehavior != null) {
			stopBehavior.applyNewStatus(ldioComponent);
		}
	}

	@EventListener(condition = "#event.status().name() == 'HALTED'")
	public void handlePipelineHaltedEvent(PipelineStatusEvent event) {
		final T ldioComponent = ldioComponents.get(event.pipelineId());
		if (ldioComponent != null && pauseBehavior != null) {
			pauseBehavior.applyNewStatus(ldioComponent);
		}
	}

	@EventListener(condition = "#event.status().name() == 'RUNNING'")
	public void handlePipelineRunningEvent(PipelineStatusEvent event) {
		final T ldioComponent = ldioComponents.get(event.pipelineId());
		if (ldioComponent != null && resumeBehavior != null) {
			resumeBehavior.applyNewStatus(ldioComponent);
		}
	}

	@EventListener
	public void handlePipelineRunningEvent(InputCreatedEvent event) {
		final T ldioComponent = ldioComponents.get(event.pipelineName());
		if (ldioComponent != null && startBehavior != null) {
			startBehavior.applyNewStatus(ldioComponent);
		}
	}

	public static final class Builder<T extends LdiComponent> {
		private PipelineStatusChangedBehavior<T> startBehavior;
		private PipelineStatusChangedBehavior<T> pauseBehavior;
		private PipelineStatusChangedBehavior<T> resumeBehavior;
		private PipelineStatusChangedBehavior<T> stopBehavior;

		public Builder<T> withStartBehavior(PipelineStatusChangedBehavior<T> startBehavior) {
			this.startBehavior = startBehavior;
			return this;
		}

		public Builder<T> withPauseBehavior(PipelineStatusChangedBehavior<T> pauseBehavior) {
			this.pauseBehavior = pauseBehavior;
			return this;
		}

		public Builder<T> withResumeBehavior(PipelineStatusChangedBehavior<T> resumeBehavior) {
			this.resumeBehavior = resumeBehavior;
			return this;
		}

		public Builder<T> withStopBehavior(PipelineStatusChangedBehavior<T> stopBehavior) {
			this.stopBehavior = stopBehavior;
			return this;
		}

		public LdioPipelineEventsListener<T> build() {
			return new LdioPipelineEventsListener<>(startBehavior, pauseBehavior, resumeBehavior, stopBehavior);
		}
	}
}
