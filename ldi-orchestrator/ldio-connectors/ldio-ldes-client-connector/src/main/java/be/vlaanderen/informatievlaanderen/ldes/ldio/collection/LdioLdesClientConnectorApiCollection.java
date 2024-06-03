package be.vlaanderen.informatievlaanderen.ldes.ldio.collection;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientConnectorApi;

import java.util.Optional;

/**
 * Keeps references to LdioLdesClientConnectorApis that are added by the configured pipelines
 */
public interface LdioLdesClientConnectorApiCollection {
	/**
	 * Gets the LdioLdesClientConnectorApi that belongs to the pipeline
	 *
	 * @param pipeline name of the pipeline
	 * @return the connector api wrapped in an optional if present, otherwise an empty optional
	 */

	Optional<LdioLdesClientConnectorApi> get(String pipeline);

	/**
	 * Adds a LdioLdesClientConnectorApi with a pipeline name to the collection
	 *
	 * @param pipeline                   name of the pipeline
	 * @param ldioLdesClientConnectorApi api to add
	 */
	void add(String pipeline, LdioLdesClientConnectorApi ldioLdesClientConnectorApi);

	/**
	 * Removes the LdioLdesClientConnectorApi when it belongs to a pipeline
	 *
	 * @param pipeline the name of the pipeline
	 * @return the found connector api wrapped in an optional if present, otherwise an empty optional
	 */
	Optional<LdioLdesClientConnectorApi> remove(String pipeline);
}
