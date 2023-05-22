package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.retry;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import io.github.resilience4j.core.IntervalBiFunction;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.core.functions.Either;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

import static org.apache.commons.lang3.Validate.notNull;

public class BasicIntervalFunctionDecorator implements IntervalBiFunction<Response> {

	private final Logger log = LoggerFactory.getLogger(BasicIntervalFunctionDecorator.class);

	private final IntervalFunction intervalFunction;

	public BasicIntervalFunctionDecorator(IntervalFunction intervalFunction) {
		this.intervalFunction = notNull(intervalFunction);
	}

	@Override
	public Long apply(Integer attempt, Either<Throwable, Response> eitherResponse) {
		final Response response = eitherResponse.isRight() ? eitherResponse.get() : null;
		log.atWarn().log(getRetryLogSupplier(attempt, response));

		return getRetryAfterHeader(response)
				.map(RetryAfter::getMillisUntilRetry)
				.orElseGet(() -> intervalFunction.apply(attempt));
	}

	private Supplier<String> getRetryLogSupplier(Integer attempt, Response response) {
		if (response != null) {
			return () -> "Retrying request (attempt #%d) to %s after receiving response code %s"
					.formatted(attempt, response.getRequestedUrl(), response.getHttpStatus());
		} else {
			return () -> "Retrying request (attempt #%d) after null response".formatted(attempt);
		}
	}

	private Optional<RetryAfter> getRetryAfterHeader(Response nullableResponse) {
		return Optional.ofNullable(nullableResponse)
				.flatMap(response -> response.getFirstHeaderValue(HttpHeaders.RETRY_AFTER))
				.map(RetryAfter::from);
	}

}
