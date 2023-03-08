package ldes.client.requestexecutor.executor.clientcredentials;

import java.time.LocalDateTime;
import java.util.Optional;

import com.github.scribejava.core.model.OAuth2AccessToken;

public class OAuth2AccessTokenExpiryWrapper {

	private final OAuth2AccessToken token;
	private final LocalDateTime expiryTs;

	public OAuth2AccessTokenExpiryWrapper(OAuth2AccessToken token, LocalDateTime expiryTs) {
		this.token = token;
		this.expiryTs = expiryTs;
	}

	public static OAuth2AccessTokenExpiryWrapper from(OAuth2AccessToken token) {
		if (token.getExpiresIn() != null) {
			final LocalDateTime expiryTs = LocalDateTime.now().plusSeconds(token.getExpiresIn());
			return new OAuth2AccessTokenExpiryWrapper(token, expiryTs);
		} else {
			return empty();
		}
	}

	public static OAuth2AccessTokenExpiryWrapper empty() {
		return new OAuth2AccessTokenExpiryWrapper(null, null);
	}

	// TODO: 6/03/2023 test
	public Optional<OAuth2AccessToken> getAccessToken() {
		if (isValid()) {
			return Optional.of(token);
		}
		return Optional.empty();
	}

	private boolean isValid() {
		return token != null && isNotExpired();
	}

	private boolean isNotExpired() {
		// 30 -> seconds to give the code some time to actually make the request.
		return LocalDateTime.now().isBefore(expiryTs.minusSeconds(30));
	}

}
