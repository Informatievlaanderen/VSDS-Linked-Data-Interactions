package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.ConfigPropertyMissingException;

import java.util.List;

public final class LdioHttpInputPollerProperties {
	public static final String URL = "url";
	public static final String INTERVAL = "interval";
	public static final String CRON = "cron";
	public static final String CONTINUE_ON_FAIL = "continueOnFail";

	public static final String INVALID_PROPERTY = "Invalid config for the ldio http in poller: %s cannot have following value: %s";
	public static final String INTERVAL_MIGRATION_WARNING = "'interval' property is deprecated. Please consider migrating to 'cron' property";
	private final List<GetRequest> requests;
	private final PollingInterval pollingInterval;
	private final boolean continueOnFail;

	public LdioHttpInputPollerProperties(List<String> endpoints, PollingInterval pollingInterval, boolean continueOnFail) {
		this.requests = endpoints.stream().map(endpoint -> new GetRequest(endpoint, RequestHeaders.empty())).toList();
		this.pollingInterval = pollingInterval;
		this.continueOnFail = continueOnFail;
	}

	public static LdioHttpInputPollerProperties fromComponentProperties(ComponentProperties properties) {
		final List<String> endpoints = extractEndpoints(properties);
		final boolean continueOnFail = properties.getOptionalBoolean(CONTINUE_ON_FAIL).orElse(true);
		final PollingInterval pollingInterval = properties.getOptionalProperty(CRON)
				.map(PollingInterval::withCron)
				.orElseGet(() -> PollingInterval.withInterval(properties.getProperty(INTERVAL)));
		return new LdioHttpInputPollerProperties(endpoints, pollingInterval, continueOnFail);
	}

	private static List<String> extractEndpoints(ComponentProperties properties) {
		final List<String> endpoints = properties.getPropertyList(URL);
		if (endpoints.isEmpty()) {
			throw new ConfigPropertyMissingException(properties.getPipelineName(), properties.getComponentName(), URL);
		}
		return endpoints;
	}

	public List<GetRequest> getRequests() {
		return requests;
	}

	public PollingInterval getPollingInterval() {
		return pollingInterval;
	}

	public boolean isContinueOnFailEnabled() {
		return continueOnFail;
	}

}
