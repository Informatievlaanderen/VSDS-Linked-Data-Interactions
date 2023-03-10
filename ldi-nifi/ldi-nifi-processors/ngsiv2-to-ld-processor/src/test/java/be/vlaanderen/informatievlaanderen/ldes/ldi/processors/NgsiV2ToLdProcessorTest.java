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

@WireMockTest(httpPort = 10101)
class NgsiV2ToLdProcessorTest {
	private final TestRunner testRunner = TestRunners.newTestRunner(NgsiV2ToLdProcessor.class);

	@Test
	void when_translatingNgsiV2ToLD_expectSuccess() throws IOException {
		String CORE_CONTEXT = "http://localhost:10101/ngsi-ld-core-context.json";
		String LD_CONTEXT = "http://localhost:10101/water-quality-observed-context.json";

		String ngsiV2Content = Files.readString(Path.of("src/test/resources/ngsiV2_wqo_input.json"));

		testRunner.setProperty("CORE_CONTEXT", CORE_CONTEXT);
		testRunner.setProperty("LD_CONTEXT", LD_CONTEXT);

		testRunner.enqueue(ngsiV2Content, Map.of("mime.type", "application/json"));
		testRunner.run();

		testRunner.assertQueueEmpty();
		testRunner.assertTransferCount(FlowManager.SUCCESS, 1);
	}
}
