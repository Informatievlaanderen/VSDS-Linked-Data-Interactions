package be.vlaanderen.informatievlaanderen.ldes.client.state;

import be.vlaanderen.informatievlaanderen.ldes.client.config.LdesClientConfig;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest(httpPort = LdesStateManagerTest.HTTP_PORT)
class NonPersistedLdesStateManagerTest extends LdesStateManagerTest {

	LdesClientConfig config;

	public LdesClientConfig getConfig() {
		if (config == null) {
			config = new LdesClientConfig(PERSISTENCE_NONE);
		}

		return config;
	}

	@Test
	void whenUsingNonPersistedState_thenCorrectBeanIsLoaded() {
		assertEquals(NonPersistedLdesStateManager.class.getCanonicalName(), stateManager.getClass().getCanonicalName());
	}
}
