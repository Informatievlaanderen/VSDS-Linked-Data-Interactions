package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PollingInterval;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.scheduling.support.CronTrigger;

import java.time.Duration;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PollingInterval.TYPE.CRON;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PollingInterval.TYPE.INTERVAL;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

class PollingIntervalTest {
	@Test
	void when_withInterval_ExpectValidObject() {
		PollingInterval pollingInterval = PollingInterval.withInterval("PT1S");

		var expected = new PollingInterval(Duration.of(1, SECONDS));
		assertEquals(expected, pollingInterval);
		assertEquals(INTERVAL, pollingInterval.getType());
		assertNull(pollingInterval.getCronTrigger());
	}

	@Test
	void when_withCron_ExpectValidObject() {
		PollingInterval pollingInterval = PollingInterval.withCron("* * * * * *");

		var expected = new PollingInterval(new CronTrigger("* * * * * *"));
		assertEquals(expected, pollingInterval);
		assertEquals(CRON, pollingInterval.getType());
		assertNull(pollingInterval.getDuration());
	}

	@ParameterizedTest
	@ArgumentsSource(InvalidIntervalArgumentsProvider.class)
	void when_withInterval_withInvalidType_expectException(String interval) {
		assertThrows(IllegalArgumentException.class, () -> PollingInterval.withInterval(interval));
	}

	@ParameterizedTest
	@ArgumentsSource(InvalidCronArgumentsProvider.class)
	void when_withCron_withInvalidType_expectException(String expression) {
		assertThrows(IllegalArgumentException.class, () -> PollingInterval.withCron(expression));
	}

	static class InvalidCronArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(Arguments.of("P12S"), Arguments.of("Invalid time"), Arguments.of("0 * * * *"));
		}
	}

	static class InvalidIntervalArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(Arguments.of("P12S"), Arguments.of("Invalid time"), Arguments.of("0 * * * * ?"));
		}
	}
}
