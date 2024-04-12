package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.common;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

class LdesDiscovererExecutorTest {

	@Nested
	@SpringBootTest(args = {"--url=localhost:8080/observations"})
	class NoAuth {
		@Autowired
		private LdesDiscovererExecutor executor;

		@Test
		void name() {
			executor.run();
		}
	}

	@Nested
	@SpringBootTest(args = {"--url=localhost:8080/observations", "--auth-type=API_KEY", "--api-key=my-secret-api-key"})
	class BasicAuth {
		@Autowired
		private LdesDiscovererExecutor executor;


		@Test
		void name() {
			executor.run();
		}
	}

	@Nested
	class RateLimit {

	}
}