package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.PipelineException;

import java.io.File;

public interface PipelineService {
	/**
	 * Adds a pipeline to the application
	 *
	 * @param pipeline configuration of a pipeline to add
	 * @return the pipeline configuration
	 * @throws PipelineException when something goes wrong while creating or adding the pipeline
	 */
	PipelineConfig addPipeline(PipelineConfig pipeline) throws PipelineException;

	/**
	 * Adds a pipeline to the application and persists the config to a file
	 *
	 * @param pipeline      configuration of a pipeline to add
	 * @param persistedFile file whereto the pipeline configuration must be persisted
	 * @return the pipeline configuration
	 * @throws PipelineException when something goes wrong while creating or adding the pipeline
	 */
	PipelineConfig addPipeline(PipelineConfig pipeline, File persistedFile) throws PipelineException;

	/**
	 * Deletes a pipeline when it exists
	 *
	 * @param pipeline name of the pipeline to be deleted
	 * @return <code>true</code> if deletion succeeded, otherwise <code>false</code>
	 */
	boolean requestDeletion(String pipeline);
}
