package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status;

/**
 * Representation where the pipeline status has changed
 */
public enum StatusChangeSource {
	/**
	 * When a pipeline status has changed by the application itself, e.g. when an error occurs
	 */
	AUTO,
	/**
	 * When a pipeline status has changed by the user, e.g. by calling an REST endpoint
	 */
	MANUAL
}
