package be.vlaanderen.informatievlaanderen.ldes.ldio.repositories;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO;

import java.io.File;
import java.util.List;

public interface PipelineRepository {
	List<PipelineConfigTO> findAll();

	void save(PipelineConfig pipeline);

	void save(PipelineConfig pipeline, File persistedFile);

	void delete(String pipelineName);

	boolean exists(String pipelineName);
}
