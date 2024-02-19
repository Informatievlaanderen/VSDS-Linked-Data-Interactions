package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.PollingIntervalTest.InvalidCronArgumentsProvider;
import be.vlaanderen.informatievlaanderen.ldes.ldio.PollingIntervalTest.InvalidIntervalArgumentsProvider;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpInputPollerAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpInputPollerProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PollingInterval;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.MockedConstruction;
import org.springframework.scheduling.support.CronTrigger;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LdioLdioHttpInputPollerAutoConfigTest {

	private final LdiAdapter adapter = mock(LdiAdapter.class);
	private final ComponentExecutor executor = mock(ComponentExecutor.class);
	private static final String BASE_URL = "http://localhost:10101";
	private static final String ENDPOINT = "/resource";

	private static ComponentProperties createConfigWithInterval(String url, String interval, String continueOnFail) {
		return new ComponentProperties("pipelineName", "", Map.of(LdioHttpInputPollerProperties.URL, url,
				LdioHttpInputPollerProperties.INTERVAL, interval,
				LdioHttpInputPollerProperties.CONTINUE_ON_FAIL, continueOnFail));
	}

	private static ComponentProperties createConfigWithCron(String url, String cron, String continueOnFail) {
		return new ComponentProperties("pipelineName", "", Map.of(LdioHttpInputPollerProperties.URL, url,
				LdioHttpInputPollerProperties.CRON, cron,
				LdioHttpInputPollerProperties.CONTINUE_ON_FAIL, continueOnFail));
	}

	private static ComponentProperties createDefaultISOTestConfig() {
		return createConfigWithInterval(BASE_URL + ENDPOINT, "PT1S", "false");
	}

	private static ComponentProperties createDefaultCronTestConfig() {
		return createConfigWithCron(BASE_URL + ENDPOINT, "* * * * * *", "false");
	}

	@Test
	void when_ValidIntervalConfig() {
		try (MockedConstruction<LdioHttpInputPoller> ignored = mockConstruction(LdioHttpInputPoller.class)) {
			LdioHttpInputPoller poller = new LdioHttpInputPollerAutoConfig()
					.httpInputPollerConfigurator(null)
					.configure(adapter, executor, createDefaultISOTestConfig());
			verify(poller, times(1)).schedulePoller(new PollingInterval(Duration.of(1, ChronoUnit.SECONDS)));
		}
	}

	@Test
	void when_ValidCronConfig() {
		try (MockedConstruction<LdioHttpInputPoller> ignored = mockConstruction(LdioHttpInputPoller.class)) {
			LdioHttpInputPoller poller = new LdioHttpInputPollerAutoConfig()
					.httpInputPollerConfigurator(null)
					.configure(adapter, executor, createDefaultCronTestConfig());
			verify(poller, times(1)).schedulePoller(new PollingInterval(new CronTrigger("* * * * * *")));
		}
	}

	@ParameterizedTest
	@ArgumentsSource(InvalidIntervalArgumentsProvider.class)
	void whenInvalidIntervalConfigured_thenCatchException(String interval) {
		Executable configurePoller = () -> new LdioHttpInputPollerAutoConfig()
				.httpInputPollerConfigurator(null)
				.configure(adapter, executor, createConfigWithInterval(BASE_URL + ENDPOINT, interval, "false"));

		assertThrows(IllegalArgumentException.class, configurePoller);
	}

	@ParameterizedTest
	@ArgumentsSource(InvalidCronArgumentsProvider.class)
	void whenInvalidCronConfigured_thenCatchException(String cron) {
		Executable configurePoller = () -> new LdioHttpInputPollerAutoConfig()
				.httpInputPollerConfigurator(null)
				.configure(adapter, executor, createConfigWithCron(BASE_URL + ENDPOINT, cron, "false"));

		assertThrows(IllegalArgumentException.class, configurePoller);
	}

}
