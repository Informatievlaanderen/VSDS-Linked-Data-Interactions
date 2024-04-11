package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetryPropertiesTest {
	@Mock
	private ApplicationArguments arguments;
	@InjectMocks
	private RetryProperties properties;

	@Test
	void test_Defaults() {
		assertThat(properties.isRetryingDisabled()).isFalse();
		assertThat(properties.getRetryLimit()).isEqualTo(5);
		assertThat(properties.getRetryStatuses()).isEmpty();
	}

	@Test
	void test_CustomConfig() {
		when(arguments.containsOption("disable-retry")).thenReturn(true);
		when(arguments.getOptionValues("retry-limit")).thenReturn(List.of("10"));
		when(arguments.getOptionValues("retry-statuses")).thenReturn(List.of("400,404,500"));

		assertThat(properties.isRetryingDisabled()).isTrue();
		assertThat(properties.getRetryLimit()).isEqualTo(10);
		assertThat(properties.getRetryStatuses()).isEqualTo(List.of(400, 404, 500));
	}
}