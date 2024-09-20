package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.PipelineConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.web.dto.PipelineConfigTO;

import java.io.File;
import java.util.List;

public interface PipelineRepository {
	/**
	 * Retrieves a list of active pipelines.
	 *
	 * @return A list of PipelineConfigTO objects representing the active pipelines.
	 */
	List<PipelineConfigTO> getActivePipelines();

	/**
	 * Store the pipeline config in a file and add to the active pipelines.
	 *
	 * @param pipeline The pipeline config to be saved.
	 */
	void activateNewPipeline(PipelineConfig pipeline);

	/**
	 * Adds to the already saved pipeline to the active pipelines.
	 *
	 * @param pipeline The pipeline config to be saved.
	 */
	void activateExistingPipeline(PipelineConfig pipeline, File persistedFile);

	/**
	 * Deletes a pipeline given its name.
	 *
	 * @param pipelineName The name of the pipeline to be deleted.
	 * @return A boolean indicating whether the deletion was successful.
	 * Returns true if the pipeline was found and successfully deleted,
	 * false if the pipeline was not found or could not be deleted due to an IOException.
	 */
	boolean delete(String pipelineName);

	/**
	 * Checks if a pipeline exists.
	 *
	 * @param pipeline The name of the pipeline to check.
	 * @return A boolean indicating whether the pipeline exists.
	 */
	boolean exists(String pipeline);
}
