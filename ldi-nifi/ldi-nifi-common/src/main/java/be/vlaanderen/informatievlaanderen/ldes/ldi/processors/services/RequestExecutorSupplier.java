package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.retry.RetryConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorDecorator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import io.github.resilience4j.retry.Retry;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.nifi.processor.ProcessContext;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RequestExecutorProperties.*;

public class RequestExecutorSupplier {
	private final RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory(false);

	public RequestExecutor getRequestExecutor(ProcessContext context) {
		return RequestExecutorDecorator.decorate(getBaseRequestExecutor(context)).with(getRetry(context)).get();
	}

	private Retry getRetry(final ProcessContext context) {
		if (retriesEnabled(context)) {
			return RetryConfig.of(getMaxRetries(context), getStatusesToRetry(context)).getRetry();
		} else {
			return null;
		}
	}

	private RequestExecutor getBaseRequestExecutor(final ProcessContext context) {
		return switch (getAuthorizationStrategy(context)) {
			case NO_AUTH -> requestExecutorFactory.createNoAuthExecutor();
			case API_KEY -> {
				List<Header> headers = List.of(
						new BasicHeader(getApiKeyHeader(context), getApiKey(context))
				);
				yield requestExecutorFactory.createNoAuthExecutor(headers);
			}
			case OAUTH2_CLIENT_CREDENTIALS ->
					requestExecutorFactory.createClientCredentialsExecutor(getOauthClientId(context),
							getOauthClientSecret(context), getOauthTokenEndpoint(context), getOauthScope(context));
		};
	}
}
