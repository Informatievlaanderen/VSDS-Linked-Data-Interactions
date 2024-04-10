package be.vlaanderen.informatievlaanderen.ldes.ldio.collection;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientConnectorApi;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class LdioLdesClientConnectorApiCollectionImpl implements LdioLdesClientConnectorApiCollection {
	private final Map<String, LdioLdesClientConnectorApi> clientConnectorApis = new HashMap<>();

	@Override
	public Optional<LdioLdesClientConnectorApi> get(String pipeline) {
		return Optional.ofNullable(clientConnectorApis.get(pipeline));
	}

	@Override
	public void add(String pipeline, LdioLdesClientConnectorApi ldioLdesClientConnectorApi) {
		clientConnectorApis.put(pipeline, ldioLdesClientConnectorApi);
	}

	@Override
	public LdioLdesClientConnectorApi remove(String pipeline) {
		return clientConnectorApis.remove(pipeline);
	}
}
