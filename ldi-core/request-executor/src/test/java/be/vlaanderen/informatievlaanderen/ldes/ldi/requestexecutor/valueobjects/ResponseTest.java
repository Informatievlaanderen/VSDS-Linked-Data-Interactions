package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseTest {

	@ParameterizedTest
	@ArgumentsSource(IsSuccessProvider.class)
	void test_isSuccess(int statusCode, boolean isSuccess) {
		assertThat(new Response(null, List.of(), statusCode, "body").isSuccess()).isEqualTo(isSuccess);
	}

	@ParameterizedTest
	@ArgumentsSource(IsForbiddenProvider.class)
	void isForbidden(int statusCode, boolean isForbidden) {
		assertThat(new Response(null, List.of(), statusCode, "body").isForbidden()).isEqualTo(isForbidden);
	}

	static class IsSuccessProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of(199, false),
					Arguments.of(200, true),
					Arguments.of(201, true),
					Arguments.of(300, false),
					Arguments.of(400, false),
					Arguments.of(500, false)
			);
		}
	}

	static class IsForbiddenProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
			return Stream.of(
					Arguments.of(200, false),
					Arguments.of(400, false),
					Arguments.of(403, true),
					Arguments.of(500, false)
			);
		}
	}

}
