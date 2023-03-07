package ldes.client.requestexecutor.executor;

import io.github.resilience4j.retry.RetryConfig;
import ldes.client.requestexecutor.config.ApiKeyConfig;
import ldes.client.requestexecutor.config.ClientCredentialsConfig;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.executor.clientcredentials.ClientCredentialsRequestExecutor;
import ldes.client.requestexecutor.executor.clientcredentials.OAuth20ServiceTokenCacheWrapper;
import ldes.client.requestexecutor.executor.noauth.DefaultRequestExecutor;
import ldes.client.requestexecutor.executor.retry.RetryExecutor;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.List;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.httpclient.apache.ApacheHttpClient;

// TODO: 6/03/2023 test
public class RequestExecutorFactory {

	public DefaultRequestExecutor createNoAuthRequestExecutor() {
		return new DefaultRequestExecutor(HttpClientBuilder.create().disableRedirectHandling().build());
	}

	public DefaultRequestExecutor createApiKeyRequestExecutor(ApiKeyConfig apiKeyConfig) {
		final Collection<Header> headers = List
				.of(new BasicHeader(apiKeyConfig.getApiKeyHeader(), apiKeyConfig.getApiKey()));
		HttpClient client = HttpClientBuilder.create().setDefaultHeaders(headers).disableRedirectHandling().build();
		return new DefaultRequestExecutor(client);
	}

	public ClientCredentialsRequestExecutor createClientCredentialsRequestExecutor(ClientCredentialsConfig config) {
		final OAuth20ServiceTokenCacheWrapper oauthSvc = new OAuth20ServiceTokenCacheWrapper(createService(config));
		return new ClientCredentialsRequestExecutor(oauthSvc);
	}

	private OAuth20Service createService(ClientCredentialsConfig config) {
		final RequestConfig clientConfig = RequestConfig.custom().setRedirectsEnabled(false).build();
		final ApacheHttpClient apacheHttpClient = new ApacheHttpClient(
				HttpAsyncClientBuilder.create().setDefaultRequestConfig(clientConfig).build());
		final DefaultApi20 authorizationApi = createAuthorizationApi(config.getTokenEndpoint());
		return new ServiceBuilder(config.getClientId())
				.apiSecret(config.getSecret())
				.defaultScope(config.getScope())
				.httpClient(apacheHttpClient)
				.build(authorizationApi);
	}

	private DefaultApi20 createAuthorizationApi(String tokenEndpoint) {
		return new DefaultApi20() {
			@Override
			public String getAccessTokenEndpoint() {
				return tokenEndpoint;
			}

			@Override
			protected String getAuthorizationBaseUrl() {
				throw new UnsupportedOperationException("This API doesn't support a Base URL.");
			}
		};
	}

	public RequestExecutor createRetry(RequestExecutor requestExecutor) {
		final RetryConfig config = RetryConfig.<Response>custom()
				.maxAttempts(3)
				.waitDuration(Duration.ofMillis(500))
				.retryOnResult(response -> response == null || response.getHttpStatus() >= 500)
				.retryOnException(IOException.class::isInstance)
				.build();

		return new RetryExecutor(requestExecutor, config);
	}
}
