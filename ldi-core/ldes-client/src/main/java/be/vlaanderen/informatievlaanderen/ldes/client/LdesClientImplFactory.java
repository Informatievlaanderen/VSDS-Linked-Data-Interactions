package be.vlaanderen.informatievlaanderen.ldes.client;

import be.vlaanderen.informatievlaanderen.ldes.client.config.LdesClientConfig;
import be.vlaanderen.informatievlaanderen.ldes.client.services.LdesFragmentFetcher;
import be.vlaanderen.informatievlaanderen.ldes.client.services.LdesFragmentFetcherImpl;
import be.vlaanderen.informatievlaanderen.ldes.client.services.LdesService;
import be.vlaanderen.informatievlaanderen.ldes.client.services.LdesServiceImpl;
import be.vlaanderen.informatievlaanderen.ldes.client.state.LdesStateManager;
import be.vlaanderen.informatievlaanderen.ldes.client.state.NonPersistedLdesStateManager;
import be.vlaanderen.informatievlaanderen.ldes.client.state.SqlitePersistedLdesStateManager;
import org.apache.http.impl.client.HttpClients;

import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.DEFAULT_PERSISTENCE_STRATEGY;
import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.PERSISTENCE_STRATEGY_SQLITE;

public class LdesClientImplFactory {

	private LdesClientImplFactory() {
	}

	public static LdesStateManager getStateManager(LdesClientConfig config) {
		if (config.getPersistenceStrategy().equalsIgnoreCase(PERSISTENCE_STRATEGY_SQLITE)) {
			return getSqlitePersistedStateManager(config);
		}

		return getNonPersistedStateManager();
	}

	public static LdesStateManager getDefaultStateManager(LdesClientConfig config) {
		if (DEFAULT_PERSISTENCE_STRATEGY.equalsIgnoreCase(PERSISTENCE_STRATEGY_SQLITE)) {
			return getSqlitePersistedStateManager(config);
		}

		return getNonPersistedStateManager();
	}

	private static LdesStateManager getNonPersistedStateManager() {
		return new NonPersistedLdesStateManager();
	}

	private static LdesStateManager getSqlitePersistedStateManager(LdesClientConfig config) {
		return new SqlitePersistedLdesStateManager(config);
	}

	public static LdesFragmentFetcher getFragmentFetcher(LdesClientConfig config) {
		return new LdesFragmentFetcherImpl(config, HttpClients.createDefault());
	}

	public static LdesService getLdesService() {
		return getLdesService(new LdesClientConfig());
	}

	public static LdesService getLdesService(LdesClientConfig config) {
		return getLdesService(getStateManager(config), getFragmentFetcher(config));
	}

	public static LdesService getLdesService(LdesStateManager stateManager, LdesFragmentFetcher fragmentFetcher) {
		return new LdesServiceImpl(stateManager, fragmentFetcher);
	}
}
