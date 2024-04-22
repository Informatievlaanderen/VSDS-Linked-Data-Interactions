package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.common;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.ContainsPattern;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

class LdesDiscovererExecutorTest {
	private static final int WIREMOCK_PORT = 10101;
	private static final String ENDPOINT = "/observations";


	@Nested
	@WireMockTest(httpPort = WIREMOCK_PORT)
	@SpringBootTest(args = {"--url=http://localhost:10101/observations"})
	class NoAuth {
		@Autowired
		private LdesDiscovererExecutor executor;

		@Test
		void test_discoverWithoutAuth() throws IOException {
			stubFor(get(ENDPOINT).willReturn(okForContentType("text/turtle", readDataModel())));

			executor.run();

			verify(getRequestedFor(urlEqualTo(ENDPOINT)).withoutHeader("X-API-KEY").withoutHeader("Authorization"));
		}

		@Test
		void test_discoverWithRetry() {
			stubFor(get(ENDPOINT).willReturn(serverError()));

			executor.run();

			verify(5, getRequestedFor(urlEqualTo(ENDPOINT)));
		}
	}

	@Nested
	@WireMockTest(httpPort = WIREMOCK_PORT)
	@SpringBootTest(args = {"--url=http://localhost:10101/observations", "--auth-type=API_KEY", "--api-key=my-secret-api-key"})
	class BasicAuth {
		@Autowired
		private LdesDiscovererExecutor executor;

		@Test
		void name() throws IOException {
			stubFor(get(ENDPOINT).willReturn(okForContentType("text/turtle", readDataModel())));

			executor.run();

			verify(getRequestedFor(urlEqualTo(ENDPOINT)).withHeader("X-API-KEY", new ContainsPattern("my-secret-api-key")));
		}
	}

	@Nested
	@WireMockTest(httpPort = WIREMOCK_PORT)
	@SpringBootTest(args = {"--url=http://localhost:10101/observations", "--retry-limit=3", "--rate-limit=1", "--rate-limit-period=PT5S"})
	class RateLimit {
		@Autowired
		private LdesDiscovererExecutor executor;

		@Test
		void test_discoverWithRateLimit() {
			final StopWatch stopWatch = new StopWatch();
			stubFor(get(ENDPOINT).willReturn(serverError()));

			stopWatch.start();
			executor.run();
			stopWatch.stop();

			assertThat(stopWatch.getTotalTimeSeconds()).isGreaterThanOrEqualTo(10);
			verify(3, getRequestedFor(urlEqualTo(ENDPOINT)));
		}
	}

	@Nested
	@WireMockTest(httpPort = WIREMOCK_PORT)
	@SpringBootTest(args = {"--url=http://localhost:10101/observations", "--disable-retry"})
	class RetryDisabled {
		@Autowired
		private LdesDiscovererExecutor executor;

		@Test
		void test_discoverWithoutRetry() {
			stubFor(get(ENDPOINT).willReturn(serverError()));

			executor.run();

			verify(1, getRequestedFor(urlEqualTo(ENDPOINT)));
		}
	}

	@Nested
	@WireMockTest(httpPort = WIREMOCK_PORT)
	@SpringBootTest(args = {"--url=http://localhost:10101/observations", "--source-format=application/n-quads", "--header=Connection: keep-alive", "--header=Cache-Control:no-cache", "--header=role: developer"})
	class HeadersProvided {
		@Autowired
		private LdesDiscovererExecutor executor;

		@Test
		void test_discoverWithoutRetry() {
			stubFor(get(ENDPOINT).willReturn(okForContentType("application/n-quads", "")));

			executor.run();

			verify(
					getRequestedFor(urlEqualTo(ENDPOINT))
							.withHeader("Accept", new ContainsPattern("application/n-quads"))
							.withHeader("Connection", new ContainsPattern("keep-alive"))
							.withHeader("Cache-Control", new ContainsPattern("no-cache"))
							.withHeader("role", new ContainsPattern("developer"))
			);
		}
	}


	private String readDataModel() throws IOException {
		File file = ResourceUtils.getFile("classpath:tree-relations/relation-1.ttl");
		return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
	}
}