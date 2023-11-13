package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PipelineCreatedEventListener implements ApplicationListener<PipelineCreatedEvent> {
	private final PipelineService pipelineService;

	public PipelineCreatedEventListener(PipelineService pipelineService) {
		this.pipelineService = pipelineService;
	}

	@Override
	public void onApplicationEvent(PipelineCreatedEvent event) {
		pipelineService.addPipeline(event.pipelineConfig());
	}
}
