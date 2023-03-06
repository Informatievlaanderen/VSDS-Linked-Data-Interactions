package ldes.client.requestexecutor.config;

public class ClientCredentialsConfig {

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

	public String getClientId() {
		return clientId;
	}

	public String getSecret() {
		return secret;
	}

	public String getTokenEndpoint() {
		return tokenEndpoint;
	}

	public String getScope() {
		return scope;
	}
}
