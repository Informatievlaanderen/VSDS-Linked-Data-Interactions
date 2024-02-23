package be.vlaanderen.informatievlaanderen.ldes.ldio.services.initializer;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;

import java.util.List;

public interface PipelineInitializer {
	String name();

	List<PipelineConfig> initPipelines();
}
