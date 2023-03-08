package ldes.client.requestexecutor.executor;

import io.github.resilience4j.retry.RetryConfig;
import ldes.client.requestexecutor.domain.valueobjects.ApiKeyConfig;
import ldes.client.requestexecutor.domain.valueobjects.ClientCredentialsConfig;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.executor.clientcredentials.ClientCredentialsRequestExecutor;
import ldes.client.requestexecutor.executor.clientcredentials.OAuth20ServiceTokenCacheWrapper;
import ldes.client.requestexecutor.executor.noauth.DefaultRequestExecutor;
import ldes.client.requestexecutor.executor.retry.RetryExecutor;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.List;

// TODO: 6/03/2023 test
public class RequestExecutorFactory {

	public DefaultRequestExecutor createNoAuthRequestExecutor() {
		return new DefaultRequestExecutor(HttpClientBuilder.create().disableRedirectHandling().build());
	}

	public DefaultRequestExecutor createApiKeyRequestExecutor(ApiKeyConfig apiKeyConfig) {
		final Collection<Header> headers = List.of(apiKeyConfig.createBasicHeader());
		HttpClient client = HttpClientBuilder.create().setDefaultHeaders(headers).disableRedirectHandling().build();
		return new DefaultRequestExecutor(client);
	}

	public ClientCredentialsRequestExecutor createClientCredentialsRequestExecutor(ClientCredentialsConfig config) {
		final OAuth20ServiceTokenCacheWrapper oauthSvc = new OAuth20ServiceTokenCacheWrapper(config.createService());
		return new ClientCredentialsRequestExecutor(oauthSvc);
	}

	public RequestExecutor createRetry(RequestExecutor requestExecutor) {
		final RetryConfig config = RetryConfig.<Response>custom()
				.maxAttempts(3)
				.waitDuration(Duration.ofMillis(500))
				.retryOnResult(response -> response == null || response.getHttpStatus() >= 500)
				.retryOnException(e -> e instanceof IOException)
				.build();

		return new RetryExecutor(requestExecutor, config);
	}
}
