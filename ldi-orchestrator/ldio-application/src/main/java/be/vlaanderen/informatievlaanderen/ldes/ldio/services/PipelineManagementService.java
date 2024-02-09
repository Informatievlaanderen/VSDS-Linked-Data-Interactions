package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.events.PipelineCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PipelineManagementService {
	private final List<PipelineConfig> pipelines = new ArrayList<>();

	@EventListener
	void handlePipelineCreated(PipelineCreatedEvent event) {
		this.addPipeline(event.pipelineConfig());
	}

	public void addPipeline(PipelineConfig pipeline) {
		pipelines.add(pipeline);
	}

	public List<PipelineConfig> getPipelines() {
		return pipelines;
	}
}
