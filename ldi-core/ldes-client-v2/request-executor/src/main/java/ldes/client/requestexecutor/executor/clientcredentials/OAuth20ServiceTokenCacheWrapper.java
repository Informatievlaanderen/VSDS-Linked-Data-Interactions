package ldes.client.requestexecutor.executor.clientcredentials;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.oauth.OAuth20Service;

public class OAuth20ServiceTokenCacheWrapper {

	private final OAuth20Service oAuth20Service;
	private OAuth2AccessTokenExpiryWrapper tokenExpiryWrapper = OAuth2AccessTokenExpiryWrapper.empty();

	public OAuth20ServiceTokenCacheWrapper(OAuth20Service oAuth20Service) {
		this.oAuth20Service = oAuth20Service;
	}

	public OAuth2AccessToken getAccessTokenClientCredentialsGrant() {
		return tokenExpiryWrapper.getAccessToken().orElseGet(() -> {
			try {
				OAuth2AccessToken token = oAuth20Service.getAccessTokenClientCredentialsGrant();
				tokenExpiryWrapper = OAuth2AccessTokenExpiryWrapper.from(token);
				return token;
			} catch (IOException | InterruptedException | ExecutionException e) {
				// TODO: 6/03/2023 handle exceptions properly, also needed for retries
				throw new RuntimeException(e);
			}
		});
	}

	public void signRequest(OAuth2AccessToken accessToken, OAuthRequest request) {
		oAuth20Service.signRequest(accessToken, request);
	}

	public Response execute(OAuthRequest request) throws InterruptedException, ExecutionException, IOException {
		return oAuth20Service.execute(request);
	}

}
