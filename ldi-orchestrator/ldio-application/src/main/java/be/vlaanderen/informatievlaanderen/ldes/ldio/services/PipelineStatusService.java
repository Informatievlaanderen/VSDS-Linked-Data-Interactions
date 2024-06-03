package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.StatusChangeSource;

import java.util.Map;

public interface PipelineStatusService {
	/**
	 * @param pipelineName name of the pipeline of which the status must be fetched
	 * @return the status of the pipeline
	 */
	PipelineStatus getPipelineStatus(String pipelineName);

	/**
	 * @param pipelineName name of the pipeline of which the source of the status change must be fetched
	 * @return the last status change source of the pipeline
	 */
	StatusChangeSource getPipelineStatusChangeSource(String pipelineName);

	/**
	 * Resume the pipeline if halted
	 *
	 * @param pipelineId name of the halted pipeline that must be resumed
	 * @return the updated pipeline status
	 */
	PipelineStatus resumeHaltedPipeline(String pipelineId);

	/**
	 * Halt the pipeline if running
	 *
	 * @param pipelineId name of the pipeline that must be paused
	 * @return the updated pipeline status
	 */
	PipelineStatus haltRunningPipeline(String pipelineId);

	/**
	 * Stops the pipeline completely
	 *
	 * @param pipelineId name of the pipeline that must be stopped
	 * @return the updated pipeline status
	 */
	PipelineStatus stopPipeline(String pipelineId);

	/**
	 * Fetches an overview of the existing pipelines with their status
	 *
	 * @return a map with the pipeline name as key and the belonging pipeline status as value
	 */
	Map<String, PipelineStatus> getPipelineStatusOverview();
}
