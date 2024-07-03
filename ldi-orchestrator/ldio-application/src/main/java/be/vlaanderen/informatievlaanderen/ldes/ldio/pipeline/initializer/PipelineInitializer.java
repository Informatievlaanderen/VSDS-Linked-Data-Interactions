package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.initializer;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.PipelineConfig;

import java.util.List;

public interface PipelineInitializer {
	String name();

	List<PipelineConfig> initPipelines();
}
