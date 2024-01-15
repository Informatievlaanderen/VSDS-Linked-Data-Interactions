package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineCreatedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PipelineCreatedEventListener implements ApplicationListener<PipelineCreatedEvent> {
	private final PipelineCreationService pipelineCreationService;

	public PipelineCreatedEventListener(PipelineCreationService pipelineCreationService) {
		this.pipelineCreationService = pipelineCreationService;
	}

	@Override
	public void onApplicationEvent(PipelineCreatedEvent event) {
		pipelineCreationService.addPipeline(event.pipelineConfig());
	}
}
