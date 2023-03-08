package ldes.client.requestexecutor.domain.valueobjects;

import ldes.client.requestexecutor.executor.noauth.DefaultRequestExecutor;
import org.apache.http.message.BasicHeader;

import java.util.List;

public class ApiKeyConfig {

	private final String apiKeyHeader;
	private final String apiKey;

	public ApiKeyConfig(String apiKeyHeader, String apiKey) {
		this.apiKeyHeader = apiKeyHeader;
		this.apiKey = apiKey;
	}

	public DefaultRequestExecutor createRequestExecutor() {
		return new DefaultConfig().createRequestExecutor(List.of(createBasicHeader()));
	}

	private BasicHeader createBasicHeader() {
		return new BasicHeader(apiKeyHeader, apiKey);
	}

}
