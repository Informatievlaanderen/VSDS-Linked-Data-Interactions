package be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor;

public class RequestExecutorProperties {

    private RequestExecutorProperties() {
    }

    public static final String HTTP_HEADERS = "http.headers";
    public static final String HTTP_HEADERS_KEY = "key";
    public static final String HTTP_HEADERS_VALUE = "value";
    public static final String HTTP_METHOD = "http.method";
    public static final String HTTP_CONTENT_TYPE = "http.content-type";

    public static final String RETRIES_ENABLED = "retries.enabled";
    public static final String MAX_RETRIES = "retries.max";
    public static final String STATUSES_TO_RETRY = "retries.statuses-to-retry";

    public static final String RATE_LIMIT_ENABLED = "rate-limit.enabled";

    /**
     * @deprecated To allow more customization, to configure a limit for a certain period for example,
     * we recommend to use <code>rate-limit.limit</code> and <code>rate-limit.period instead</code>
     */
    @Deprecated(since = "1.14.0")
    public static final String MAX_REQUESTS_PER_MINUTE = "rate-limit.max-requests-per-minute";
    public static final String RATE_LIMIT_LIMIT = "rate-limit.limit";
    public static final String RATE_LIMIT_PERIOD = "rate-limit.period";
    public static final String DEFAULT_RATE_LIMIT_PERIOD = "PT1M";
    // authorization properties
    public static final String AUTH_TYPE = "auth.type";
    public static final String API_KEY = "auth.api-key";
    public static final String API_KEY_HEADER = "auth.api-key-header";
    public static final String CLIENT_ID = "auth.client-id";
    public static final String CLIENT_SECRET = "auth.client-secret";
    public static final String TOKEN_ENDPOINT = "auth.token-endpoint";

}
