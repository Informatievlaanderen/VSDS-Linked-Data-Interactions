package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArgumentsTest {
	@Mock
	private ApplicationArguments applicationArguments;
	@InjectMocks
	private Arguments arguments;


	@Test
	void containsFlag_WhenFlagExists_ReturnsTrue() {
		final String flagKey = "enable-flag";
		when(applicationArguments.containsOption(flagKey)).thenReturn(true);

		boolean containsFlag = arguments.containsFlag(flagKey);

		assertThat(containsFlag).isTrue();
	}

	@Test
	void containsFlag_WhenFlagDoesNotExist_ReturnsFalse() {
		boolean containsFlag = arguments.containsFlag("test");

		assertThat(containsFlag).isFalse();
	}

	@Test
	void getArgumentValues_WhenValuesExist_ReturnsValuesList() {
		final String headerKey = "header";
		final List<String> headers = List.of("Content-Type: application/json", "Accept: application/json");
		when(applicationArguments.getOptionValues(headerKey)).thenReturn(headers);

		List<String> argumentValues = arguments.getArgumentValues(headerKey);

		assertThat(argumentValues).containsExactlyInAnyOrderElementsOf(headers);
	}

	@Test
	void getArgumentValues_WhenValuesDoNotExist_ReturnsEmptyList() {
		List<String> argumentValues = arguments.getArgumentValues("not-provided");

		assertThat(argumentValues).isEmpty();
	}

	@Test
	void getValue_WhenValueExists_ReturnsOptionalWithValue() {
		final String key = "key";
		final String value = "value";
		when(applicationArguments.getOptionValues(key)).thenReturn(List.of("value"));

		Optional<String> actual = arguments.getValue(key);

		assertThat(actual).isPresent().contains(value);
	}

	@Test
	void getValue_WhenValueDoesNotExist_ReturnsEmptyOptional() {
		Optional<String> value = arguments.getValue("empty-value");

		assertThat(value).isEmpty();
	}

	@Test
	void getRequiredValue_WhenValueExists_ReturnsValue() {
		final String key = "required-key";
		final String value = "value";
		when(applicationArguments.getOptionValues(key)).thenReturn(List.of(value));

		String requiredValue = arguments.getRequiredValue(key);

		assertThat(requiredValue).isEqualTo(value);
	}

	@Test
	void getRequiredValue_WhenValueDoesNotExist_ThrowsIllegalArgumentException() {
		final String key = "my-absent-key";
		assertThatThrownBy(() -> arguments.getRequiredValue(key))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Missing configuration for %s", key);
	}

	@Test
	void getInteger_WhenValueExists_ReturnsOptionalWithValue() {
		final String key = "max-age";
		when(applicationArguments.getOptionValues(key)).thenReturn(List.of("123"));

		Optional<Integer> integerValue = arguments.getInteger(key);

		assertThat(integerValue).contains(123);
	}

	@Test
	void getInteger_WhenValueDoesNotExist_ReturnsEmptyOptional() {
		Optional<Integer> integerValue = arguments.getInteger("max-age");

		assertThat(integerValue).isEmpty();
	}

	@Test
	void getInteger_WhenValueIsInvalid_ThrowException() {
		final String key = "invalid-max-age";
		when(applicationArguments.getOptionValues(key)).thenReturn(List.of("abc"));

		assertThatThrownBy(() -> arguments.getInteger(key))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid configuration for invalid-max-age: unable to parse number For input string: \"abc\"");
	}
}
