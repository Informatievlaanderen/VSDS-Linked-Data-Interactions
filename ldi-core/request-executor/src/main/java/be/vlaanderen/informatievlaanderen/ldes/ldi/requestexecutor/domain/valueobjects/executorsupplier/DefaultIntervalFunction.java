package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.executorsupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.executorsupplier.headers.RetryAfter;
import io.github.resilience4j.core.IntervalBiFunction;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.core.functions.Either;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

public class DefaultIntervalFunction implements IntervalBiFunction<Response> {

	private final Logger log = LoggerFactory.getLogger(DefaultIntervalFunction.class);

	private final IntervalFunction intervalFunction;

	public DefaultIntervalFunction(IntervalFunction intervalFunction) {
		this.intervalFunction = intervalFunction;
	}

	// TODO: 16/05/2023 test
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
