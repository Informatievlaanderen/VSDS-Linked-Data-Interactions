package ldes.client.requestexecutor.config;

public class ApiKeyConfig {

    private final String apiKeyHeader;
    private final String apiKey;

    public ApiKeyConfig(String apiKeyHeader, String apiKey) {
        this.apiKeyHeader = apiKeyHeader;
        this.apiKey = apiKey;
    }

    public String getApiKeyHeader() {
        return apiKeyHeader;
    }

    public String getApiKey() {
        return apiKey;
    }
}
