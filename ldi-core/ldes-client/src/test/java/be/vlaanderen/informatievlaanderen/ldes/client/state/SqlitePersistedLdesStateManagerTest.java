package be.vlaanderen.informatievlaanderen.ldes.client.state;

import be.vlaanderen.informatievlaanderen.ldes.client.config.LdesClientConfig;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest(httpPort = LdesStateManagerTest.HTTP_PORT)
class SqlitePersistedLdesStateManagerTest extends LdesStateManagerTest {

	LdesClientConfig config;

	public LdesClientConfig getConfig() {
		if (config == null) {
			config = new LdesClientConfig(PERSISTENCE_SQLITE);
		}

		return config;
	}

	@Test
	void whenUsingSqlitePersistedState_thenCorrectBeanIsLoaded() {
		assertEquals(SqlitePersistedLdesStateManager.class.getCanonicalName(),
				stateManager.getClass().getCanonicalName());
	}
}
