package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutorSupplier;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth.DefaultConfig;
import org.apache.http.message.BasicHeader;

import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

public class ApiKeyConfig implements RequestExecutorSupplier {

	private final String apiKeyHeader;
	private final String apiKey;

	public ApiKeyConfig(String apiKeyHeader, String apiKey) {
		this.apiKeyHeader = notNull(apiKeyHeader, "apiKeyHeader cannot be null");
		this.apiKey = notNull(apiKey, "apiKey cannot be null");
	}

	public RequestExecutor createRequestExecutor() {
		return new DefaultConfig().createRequestExecutor(List.of(createBasicHeader()));
	}

	private BasicHeader createBasicHeader() {
		return new BasicHeader(apiKeyHeader, apiKey);
	}

}
