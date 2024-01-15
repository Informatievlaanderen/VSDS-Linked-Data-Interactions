package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PipelineCreationService {
	private final List<PipelineConfig> pipelines = new ArrayList<>();

	public void addPipeline(PipelineConfig pipeline) {
		pipelines.add(pipeline);
	}

	public List<PipelineConfig> getPipelines() {
		return pipelines;
	}
}
