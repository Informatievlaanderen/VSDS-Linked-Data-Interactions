package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.clientcredentials;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutorSupplier;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

import java.util.Collection;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.httpclient.apache.ApacheHttpClient;

import static org.apache.commons.lang3.Validate.notNull;

public class ClientCredentialsConfig implements RequestExecutorSupplier {

	private final String clientId;
	private final String secret;
	private final String tokenEndpoint;

	public ClientCredentialsConfig(String clientId, String secret, String tokenEndpoint) {
		this.clientId = notNull(clientId);
		this.secret = notNull(secret);
		this.tokenEndpoint = notNull(tokenEndpoint);
	}

	public RequestExecutor createRequestExecutor(Collection<Header> customHeaders) {
		return new ClientCredentialsRequestExecutor(new OAuth20ServiceTokenCacheWrapper(createService()), customHeaders);
	}

	private OAuth20Service createService() {
		final RequestConfig clientConfig = RequestConfig.custom().setRedirectsEnabled(false).build();
		final ApacheHttpClient apacheHttpClient = new ApacheHttpClient(
				HttpAsyncClientBuilder.create().setDefaultRequestConfig(clientConfig).build());
		final DefaultApi20 authorizationApi = createAuthorizationApi(tokenEndpoint);
		return new ServiceBuilder(clientId)
				.apiSecret(secret)
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
