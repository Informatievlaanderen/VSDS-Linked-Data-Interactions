package ldes.client.requestexecutor.domain.valueobjects;

import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.requestexecutor.executor.clientcredentials.ClientCredentialsRequestExecutor;
import ldes.client.requestexecutor.executor.clientcredentials.OAuth20ServiceTokenCacheWrapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.httpclient.apache.ApacheHttpClient;

public class ClientCredentialsConfig implements RequestExecutorSupplier {

	private final String clientId;
	private final String secret;
	private final String tokenEndpoint;
	private final String scope;

	public ClientCredentialsConfig(String clientId, String secret, String tokenEndpoint, String scope) {
		this.clientId = clientId;
		this.secret = secret;
		this.tokenEndpoint = tokenEndpoint;
		this.scope = scope;
	}

	public RequestExecutor createRequestExecutor() {
		return new ClientCredentialsRequestExecutor(new OAuth20ServiceTokenCacheWrapper(createService()));
	}

	private OAuth20Service createService() {
		final RequestConfig clientConfig = RequestConfig.custom().setRedirectsEnabled(false).build();
		final ApacheHttpClient apacheHttpClient = new ApacheHttpClient(
				HttpAsyncClientBuilder.create().setDefaultRequestConfig(clientConfig).build());
		final DefaultApi20 authorizationApi = createAuthorizationApi(tokenEndpoint);
		return new ServiceBuilder(clientId)
				.apiSecret(secret)
				.defaultScope(scope)
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

}
