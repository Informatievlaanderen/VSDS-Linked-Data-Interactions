package be.vlaanderen.informatievlaanderen.ldes.ldio.collection;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientConnectorApi;

import java.util.Optional;

public interface LdioLdesClientConnectorApiCollection {
	Optional<LdioLdesClientConnectorApi> get(String pipeline);

	void add(String pipeline, LdioLdesClientConnectorApi ldioLdesClientConnectorApi);

	LdioLdesClientConnectorApi remove(String pipeline);
}
