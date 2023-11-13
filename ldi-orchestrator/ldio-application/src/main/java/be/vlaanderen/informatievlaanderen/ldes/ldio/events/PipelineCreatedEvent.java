package be.vlaanderen.informatievlaanderen.ldes.ldio.events;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import org.springframework.context.ApplicationEvent;

public final class PipelineCreatedEvent extends ApplicationEvent {
	private final PipelineConfig pipelineConfig;

	public PipelineCreatedEvent(Object source, PipelineConfig pipelineConfig) {
		super(source);
		this.pipelineConfig = pipelineConfig;
	}

	public PipelineConfig pipelineConfig() {
		return pipelineConfig;
	}

}
