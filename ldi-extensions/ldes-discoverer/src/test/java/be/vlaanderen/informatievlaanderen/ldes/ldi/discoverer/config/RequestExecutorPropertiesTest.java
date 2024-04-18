package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.AuthStrategy;
import org.junit.jupiter.api.Nested;
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
class RequestExecutorPropertiesTest {
	@Mock
	private ApplicationArguments arguments;
	@InjectMocks
	private RequestExecutorProperties executorProperties;

	@Nested
	class AuthProperties {
		@Test
		void given_NoAuthType_when_GetAuthStrategy_then_ReturnDefault() {
			final AuthStrategy actual = executorProperties.getAuthStrategy();

			assertThat(actual).isEqualTo(AuthStrategy.NO_AUTH);
		}

		@Test
		void given_ApiKeyAuthConfig_when_GetAuthStrategy_then_ReturnOAuth2Strategy() {
			when(arguments.getOptionValues("auth-type")).thenReturn(List.of(AuthStrategy.API_KEY.name()));

			final AuthStrategy actual = executorProperties.getAuthStrategy();

			assertThat(actual).isEqualTo(AuthStrategy.API_KEY);
		}


	}
}
