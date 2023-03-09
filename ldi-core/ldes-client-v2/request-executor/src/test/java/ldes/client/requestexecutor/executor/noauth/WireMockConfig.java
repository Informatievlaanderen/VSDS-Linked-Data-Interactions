package ldes.client.requestexecutor.executor.noauth;

import io.cucumber.java.After;
import io.cucumber.java.Before;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

public class WireMockConfig {

	private static final int WIREMOCK_PORT = 10101;
	public static final WireMockServer wireMockServer = new WireMockServer(
			WireMockConfiguration.options().port(WIREMOCK_PORT));

	@Before
	public static void setupWireMock() {
		System.out.println("----- Starting Wiremock Server -----");
		if (!wireMockServer.isRunning()) {
			wireMockServer.start();
		}
	}

	@After
	public static void tearDownWireMock() {
		System.out.println("----- Stopping Wiremock Server -----");
		wireMockServer.stop();
	}

}
