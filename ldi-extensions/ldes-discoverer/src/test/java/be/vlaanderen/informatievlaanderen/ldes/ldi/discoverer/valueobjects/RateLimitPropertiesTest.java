package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitPropertiesTest {
	@Mock
	private ApplicationArguments arguments;
	@InjectMocks
	private RateLimitProperties properties;

	@Test
	void test_Defaults() {
		assertThat(properties.isRateLimitEnabled()).isFalse();
		assertThat(properties.getRateLimit()).isEqualTo(500);
		assertThat(properties.getRateLimitPeriod()).isEqualTo(Duration.ofMinutes(1));
	}

	@Test
	void test_CustomConfig() {
		when(arguments.containsOption("enable-rate-limit")).thenReturn(true);
		when(arguments.getOptionValues("rate-limit")).thenReturn(List.of("250"));
		when(arguments.getOptionValues("rate-limit-period")).thenReturn(List.of("PT1S"));

		assertThat(properties.isRateLimitEnabled()).isTrue();
		assertThat(properties.getRateLimit()).isEqualTo(250);
		assertThat(properties.getRateLimitPeriod()).isEqualTo(Duration.ofSeconds(1));
	}

	@Test
	void test_InvalidPeriod() {
		when(arguments.getOptionValues("rate-limit-period")).thenReturn(List.of("P1S"));

		assertThatThrownBy(() -> properties.getRateLimitPeriod())
				.isInstanceOf(IllegalArgumentException.class)
				.hasCauseInstanceOf(DateTimeParseException.class)
				.hasMessage("Illegal value for rate-limit-period:P1S");

	}
}