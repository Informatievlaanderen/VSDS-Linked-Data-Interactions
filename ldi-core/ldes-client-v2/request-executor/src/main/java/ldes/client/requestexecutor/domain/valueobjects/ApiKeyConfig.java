package ldes.client.requestexecutor.domain.valueobjects;

import org.apache.http.message.BasicHeader;

public class ApiKeyConfig {

	private final String apiKeyHeader;
	private final String apiKey;

	public ApiKeyConfig(String apiKeyHeader, String apiKey) {
		this.apiKeyHeader = apiKeyHeader;
		this.apiKey = apiKey;
	}

	public BasicHeader createBasicHeader() {
		return new BasicHeader(apiKeyHeader, apiKey);
	}

}
