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
class NgsiV2ToLdProcessorTest {
	private final TestRunner testRunner = TestRunners.newTestRunner(NgsiV2ToLdProcessor.class);

	@Test
	void when_translatingNgsiV2ToLD_expectSuccess() throws IOException {
		final String DATA_IDENTIFIER = "data";
		final String CORE_CONTEXT = "http://localhost:10101/ngsi-ld-core-context.json";
		final String LD_CONTEXT = "http://localhost:10101/water-quality-observed-context.json";

		final String ngsiV2Content = Files.readString(Path.of("src/test/resources/ngsiV2_wqo_input.json"));

		testRunner.setProperty("DATA_IDENTIFIER", DATA_IDENTIFIER);
		testRunner.setProperty("CORE_CONTEXT", CORE_CONTEXT);
		testRunner.setProperty("LD_CONTEXT", LD_CONTEXT);

		testRunner.enqueue(ngsiV2Content, Map.of("mime.type", "application/json"));
		testRunner.run();

		testRunner.assertQueueEmpty();
		testRunner.assertTransferCount(FlowManager.SUCCESS, 1);
	}

	@Test
	void testFailFlow() throws Exception {
		final String DATA_IDENTIFIER = "data";
		final String CORE_CONTEXT = "http://localhost:10101/ngsi-ld-core-context.json";
		final String LD_CONTEXT = "http://localhost:10101/water-quality-observed-context.json";

		final String ngsiV2Content = Files.readString(Path.of("src/test/resources/ngsiV2_wqo_input.json"));

		testRunner.setProperty("DATA_IDENTIFIER", DATA_IDENTIFIER);
		testRunner.setProperty("CORE_CONTEXT", CORE_CONTEXT);
		testRunner.setProperty("LD_CONTEXT", LD_CONTEXT);

		testRunner.enqueue(ngsiV2Content, Map.of("mime.type", "wrong type"));
		testRunner.run();

		testRunner.assertQueueEmpty();
		testRunner.assertTransferCount(FAILURE, 1);
	}
}
