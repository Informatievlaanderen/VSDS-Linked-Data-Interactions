package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.HttpInputPollerAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.HttpInputPollerProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.MockedConstruction;

import java.util.Map;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class HttpInputPollerAutoConfigTest {

	private final LdiAdapter adapter = mock(LdiAdapter.class);
	private final ComponentExecutor executor = mock(ComponentExecutor.class);
	private static final String BASE_URL = "http://localhost:10101";
	private static final String ENDPOINT = "/resource";

	@Test
	void when_ValidConfig() {
		try (MockedConstruction<HttpInputPoller> ignored = mockConstruction(HttpInputPoller.class)) {
			HttpInputPoller poller = new HttpInputPollerAutoConfig()
					.httpInputPollerConfigurator()
					.configure(adapter, executor, createDefaultTestConfig());
			verify(poller, times(1)).schedulePoller(1);
		}
	}

	@ParameterizedTest
	@ArgumentsSource(InvalidIntervalArgumentsProvider.class)
	void whenInvalidIntervalConfigured_thenCatchException(String interval) {
		Executable configurePoller = () -> new HttpInputPollerAutoConfig()
				.httpInputPollerConfigurator()
				.configure(adapter, executor, createConfig(BASE_URL + ENDPOINT, interval, "false"));

		assertThrows(IllegalArgumentException.class, configurePoller);
	}

	static class InvalidIntervalArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(Arguments.of("P12S"), Arguments.of("Invalid time"), Arguments.of("0 * * * * ?"));
		}
	}

	private static ComponentProperties createConfig(String url, String interval, String continueOnFail) {
		return new ComponentProperties(Map.of(PIPELINE_NAME, "pipeline", HttpInputPollerProperties.URL, url,
				HttpInputPollerProperties.INTERVAL, interval,
				HttpInputPollerProperties.CONTINUE_ON_FAIL, continueOnFail));
	}

	private static ComponentProperties createDefaultTestConfig() {
		return createConfig( BASE_URL + ENDPOINT, "PT1S", "false");
	}

}
