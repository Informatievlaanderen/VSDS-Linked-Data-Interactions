package be.vlaanderen.informatievlaanderen.ldes.client.cli.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class EndpointChecker {
	private static final Logger LOGGER = LoggerFactory.getLogger(EndpointChecker.class);
	private final String endpoint;

	public EndpointChecker(final String endpoint) {
		this.endpoint = endpoint;
	}

	public boolean isReachable() {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
			connection.setRequestMethod("HEAD");
			return connection.getResponseCode() == 200;
		} catch (IOException e) {
			LOGGER.info("Endpoint {} not available", endpoint);
			return false;
		}
	}
}
