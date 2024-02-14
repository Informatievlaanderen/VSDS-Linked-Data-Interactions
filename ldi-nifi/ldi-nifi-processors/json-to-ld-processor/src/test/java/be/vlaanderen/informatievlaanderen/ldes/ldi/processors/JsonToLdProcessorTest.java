package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;

@WireMockTest(httpPort = 10101)
class JsonToLdProcessorTest {
	private final TestRunner testRunner = TestRunners.newTestRunner(JsonToLdProcessor.class);

	@Test
	void when_addingContext_expectSuccess() throws IOException {
		final String CORE_CONTEXT = "http://localhost:10101/ngsi-ld-core-context.json";

		final String content = Files.readString(Path.of("src/test/resources/input.json"));

		testRunner.setProperty("CORE_CONTEXT", CORE_CONTEXT);

		testRunner.enqueue(content, Map.of("mime.type", "application/json"));
		testRunner.run();

		testRunner.assertQueueEmpty();
		testRunner.assertTransferCount(FlowManager.SUCCESS, 1);
	}

	@Test
	void testFailFlow() throws Exception {
		final String CORE_CONTEXT = "http://localhost:10101/ngsi-ld-core-context.json";

		final String content = Files.readString(Path.of("src/test/resources/input.json"));

		testRunner.setProperty("CORE_CONTEXT", CORE_CONTEXT);

		testRunner.enqueue(content, Map.of("mime.type", "wrong type"));
		testRunner.run();

		testRunner.assertQueueEmpty();
		testRunner.assertTransferCount(FAILURE, 1);
	}
}
