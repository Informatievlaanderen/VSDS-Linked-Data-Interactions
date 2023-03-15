package ldes.client.requestexecutor.domain.valueobjects.executorsupplier;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExponentialRandomBackoffConfigTest {

    @ParameterizedTest
    @ArgumentsSource(ExponentialRandomBackoffTestProvider.class)
    void testExponentialRandomBackoff(int nrOfAttempts, int min, int max) {
        var config = new ExponentialRandomBackoffConfig(5).createRetryConfig();

        assertEquals(5, config.getMaxAttempts());

        long intervalTime = config.getIntervalBiFunction().apply(nrOfAttempts, null);
        // We need to assert an interval because of the randomization factor
        assertTrue(intervalTime >= min && intervalTime <= max);
    }

    static class ExponentialRandomBackoffTestProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            // values calculated from defaults in io.github.resilience4j.core.IntervalFunction
            return Stream.of(
                    Arguments.of(1, 250, 750),
                    Arguments.of(10, 9605, 28815),
                    Arguments.of(15, 72936, 218810)
            );
        }
    }

}