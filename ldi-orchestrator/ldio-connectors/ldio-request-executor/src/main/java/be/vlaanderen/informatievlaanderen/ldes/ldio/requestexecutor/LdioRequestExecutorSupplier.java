package be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.ratelimiter.RateLimiterConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.retry.RetryConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorDecorator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.AuthStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.ComponentProperties;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.AuthStrategy.NO_AUTH;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor.RequestExecutorProperties.*;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

/**
 * Creates a RequestExecutor based on the config provided using LDIO
 * ComponentProperties.
 */
public class LdioRequestExecutorSupplier {

    public static final String DEFAULT_API_KEY_HEADER = "X-API-KEY";

    private final RequestExecutorFactory requestExecutorFactory;

    public LdioRequestExecutorSupplier() {
        this(new RequestExecutorFactory());
    }

    public LdioRequestExecutorSupplier(RequestExecutorFactory requestExecutorFactory) {
        this.requestExecutorFactory = requestExecutorFactory;
    }

    public RequestExecutor getRequestExecutor(ComponentProperties props) {
        final RequestExecutor baseRequestExecutor = getBaseRequestExecutor(props);
        Retry retry = getRetry(props);
        RateLimiter rateLimiter = getRateLimiter(props);
        return RequestExecutorDecorator.decorate(baseRequestExecutor).with(retry).with(rateLimiter).get();
    }

    private RateLimiter getRateLimiter(ComponentProperties props) {
        boolean rateLimitEnabled = props.getOptionalBoolean(RATE_LIMIT_ENABLED).orElse(Boolean.FALSE);
        if (!rateLimitEnabled) {
            return null;
        }

        return props.getOptionalInteger(MAX_REQUESTS_PER_MINUTE)
                .map(maxRequestsPerMinute -> RateLimiterConfig.limitPerMinute(maxRequestsPerMinute).getRateLimiter())
                .orElseGet(() -> {
                    final String period = props.getOptionalProperty(RATE_LIMIT_PERIOD).orElse(DEFAULT_RATE_LIMIT_PERIOD);
                    final int limit = props.getOptionalInteger(RATE_LIMIT_LIMIT).orElse(500);
                    return RateLimiterConfig.limitForPeriod(limit, period).getRateLimiter();
                });
    }

    private Retry getRetry(ComponentProperties props) {
        boolean retriesEnabled = props.getOptionalBoolean(RETRIES_ENABLED).orElse(Boolean.TRUE);
        if (retriesEnabled) {
            int maxRetries = props.getOptionalInteger(MAX_RETRIES).orElse(5);
            List<Integer> statusesToRetry = props.getOptionalProperty(STATUSES_TO_RETRY)
                    .map(csv -> Stream.of(csv.split(",")).map(String::trim).map(Integer::parseInt).toList())
                    .orElse(new ArrayList<>());
            return RetryConfig.of(maxRetries, statusesToRetry).getRetry();
        } else {
            return null;
        }
    }

    private RequestExecutor getBaseRequestExecutor(ComponentProperties componentProperties) {
        Optional<AuthStrategy> authentication = AuthStrategy
                .from(componentProperties.getOptionalProperty(AUTH_TYPE).orElse(NO_AUTH.name()));
        final List<Header> headers = getHttpHeaders(componentProperties);

        if (authentication.isPresent()) {
            return switch (authentication.get()) {
                case NO_AUTH -> requestExecutorFactory.createNoAuthExecutor(headers);
                case API_KEY -> {
                    String apiKeyHeader = componentProperties
                            .getOptionalProperty(API_KEY_HEADER)
                            .orElse(DEFAULT_API_KEY_HEADER);
                    String apiKey = componentProperties.getProperty(API_KEY);

                    List<Header> headersWithApiKey = new ArrayList<>(headers);
                    headersWithApiKey.add(new BasicHeader(apiKeyHeader, apiKey));
                    yield requestExecutorFactory.createNoAuthExecutor(headersWithApiKey);
                }
                case OAUTH2_CLIENT_CREDENTIALS -> requestExecutorFactory.createClientCredentialsExecutor(
                        headers,
                        componentProperties.getProperty(CLIENT_ID),
                        componentProperties.getProperty(CLIENT_SECRET),
                        componentProperties.getProperty(TOKEN_ENDPOINT),
                        componentProperties.getOptionalProperty(AUTH_SCOPE).orElse(null));
            };
        }
        throw new UnsupportedOperationException("Requested authentication not available: "
                + componentProperties.getOptionalProperty(AUTH_TYPE).orElse("No auth type provided"));
    }

    private List<Header> getHttpHeaders(ComponentProperties componentProperties) {
        final ComponentProperties headers = componentProperties.extractNestedProperties(HTTP_HEADERS);
        final List<Header> result = new ArrayList<>();
        for (int i = 0; isNotEmpty(headers.extractNestedProperties(String.valueOf(i)).getConfig()); i++) {
            ComponentProperties headerProperties = headers.extractNestedProperties(String.valueOf(i));
            BasicHeader basicHeader = new BasicHeader(
                    headerProperties.getProperty(HTTP_HEADERS_KEY),
                    headerProperties.getProperty(HTTP_HEADERS_VALUE)
            );
            result.add(basicHeader);
        }
        return result;
    }

}
