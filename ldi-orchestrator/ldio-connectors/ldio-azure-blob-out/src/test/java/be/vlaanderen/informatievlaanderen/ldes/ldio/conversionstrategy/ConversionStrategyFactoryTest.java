package be.vlaanderen.informatievlaanderen.ldes.ldio.conversionstrategy;

import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.UnsupportedLangException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ConversionStrategyFactoryTest {
	private final ConversionStrategyFactory conversionStrategyFactory = new ConversionStrategyFactory();

	@ParameterizedTest(name = "OutputLang {0} results in the creation of ConversionStrategy {1}")
	@ArgumentsSource(LangAndConversionStrategyArgumentsProvider.class)
	void when_OutputLangIsRecognized_then_AppropriateConversionStrategyIsCreated(String outputLang,
			Class<ConversionStrategy> expectedConversionStrategy) {
		ConversionStrategy conversionStrategy = conversionStrategyFactory.getConversionStrategy(outputLang, "");

		assertEquals(expectedConversionStrategy, conversionStrategy.getClass());
	}

	@Test
	void when_OutputLangIsNotRecognized_then_UnsupportedLangExceptionIsThrown() {
		UnsupportedLangException unsupportedLangException = assertThrows(UnsupportedLangException.class,
				() -> conversionStrategyFactory.getConversionStrategy("non-existing", ""));

		assertEquals("Conversion does not support lang: non-existing", unsupportedLangException.getMessage());
	}

	static class LangAndConversionStrategyArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("json", JsonConversionStrategy.class),
					Arguments.of("n-quads", RDFSerializationConversionStrategy.class),
					Arguments.of("n-triples", RDFSerializationConversionStrategy.class),
					Arguments.of("turtle", RDFSerializationConversionStrategy.class),
					Arguments.of("jsonld", RDFSerializationConversionStrategy.class));
		}
	}
}