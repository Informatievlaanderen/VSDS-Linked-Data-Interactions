package ldes.client.performance;

import ldes.client.performance.csvwriter.CsvFile;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;

import static org.apache.commons.io.FilenameUtils.separatorsToSystem;

/**
 * This class is used to generate performance test reports on the LDES CLient.
 */
class PerformanceTest {

	private static WireMockServer wireMockServer;
	private static final TreeNodeProcessorFactory treeNodeProcessorFactory = new TreeNodeProcessorFactory();

	@BeforeAll
	static void setUp() {
		ResponseTemplateTransformer templateTransformer = new ResponseTemplateTransformer(false);
		wireMockServer = new WireMockServer(
				WireMockConfiguration.options().extensions(templateTransformer).port(10101));
		wireMockServer.start();
	}

	@AfterAll
	static void tearDown() {
		wireMockServer.stop();
	}

	@Disabled("These tests do not contain assertions and should be run manually to generate test reports.")
	@Tag("performance")
	@Test
	void compare_persistence_strategies_f10_s1000() {
		testRunner(
				separatorsToSystem("target/compare_persistence_strategies_f10_s1000.csv"),
				1000,
				List.of(TestScenario.MEMORY10, TestScenario.SQLITE10, TestScenario.POSTGRES10));
	}

	@Disabled("These tests do not contain assertions and should be run manually to generate test reports.")
	@Tag("performance")
	@Test
	void compare_persistence_strategies_f250_s1000() {
		testRunner(
				separatorsToSystem("target/compare_persistence_strategies_f250_s1000.csv"),
				1000,
				List.of(TestScenario.MEMORY250, TestScenario.SQLITE250,
						TestScenario.POSTGRES250));
	}

	@Disabled("These tests do not contain assertions and should be run manually to generate test reports.")
	@Tag("performance")
	@Test
	void compare_persistence_strategies_f10_s100_000() {
		testRunner(
				separatorsToSystem("target/compare_persistence_strategies_f10_s100_000.csv"),
				100_000,
				List.of(TestScenario.MEMORY10, TestScenario.SQLITE10, TestScenario.POSTGRES10));
	}

	@Disabled("These tests do not contain assertions and should be run manually to generate test reports.")
	@Tag("performance")
	@Test
	void memory_real_test() {
		testRunner(
				separatorsToSystem("target/memory_real_test.csv"),
				10_000,
				List.of(TestScenario.MEMORY_EXTERNAL));
	}

	@Disabled("These tests do not contain assertions and should be run manually to generate test reports.")
	@Tag("performance")
	@Test
	void compare_rdf_formats() {
		testRunner(
				separatorsToSystem("target/compare_rdf_formats.csv"),
				10_000,
				List.of(TestScenario.MEMORY_EXTERNAL_250_TURTLE,
						TestScenario.MEMORY_EXTERNAL_250_PROTOBUF,
						TestScenario.MEMORY_EXTERNAL_500_TURTLE,
						TestScenario.MEMORY_EXTERNAL_500_PROTOBUF,
						TestScenario.MEMORY_EXTERNAL_1000_TURTLE,
						TestScenario.MEMORY_EXTERNAL_1000_PROTOBUF)
		);
	}

	@Disabled("These tests do not contain assertions and should be run manually to generate test reports.")
	@Tag("performance")
	@Test
	void test_memory_f250_s100_000() {
		testRunner(
				separatorsToSystem("target/test_memory_f250_s100_000.csv"),
				100_000,
				List.of(TestScenario.MEMORY250));
	}

	// Runs 1h14m
	@Disabled("These tests do not contain assertions and should be run manually to generate test reports.")
	@Tag("performance")
	@Test
	void test_postgres_f250_s100_000() {
		testRunner(
				separatorsToSystem("target/test_postgres_f250_s100_000.csv"),
				100_000,
				List.of(TestScenario.POSTGRES250));
	}

	private void testRunner(String fileName, int testSize, List<TestScenario> scenarios) {
		final CsvFile csvFile = new CsvFile(fileName);
		scenarios.forEach(scenario -> runTest(scenario, csvFile, testSize));
		csvFile.writeToFile();
	}

	private void runTest(TestScenario test, CsvFile csvFile, int testSize) {
		final TreeNodeProcessor treeNodeProcessor = treeNodeProcessorFactory
				.createTreeNodeProcessor(test.getPersistenceStrategy(), List.of(test.getStartingEndpoint()), test.getSourceFormat());
		treeNodeProcessor.init();

		LocalDateTime lastInterval = LocalDateTime.now();
		for (int i = 1; i <= testSize; i++) {
			treeNodeProcessor.getMember();
			if (i % (testSize / 20) == 0) {
				int msIntervals = (int) ChronoUnit.MILLIS.between(lastInterval, lastInterval = LocalDateTime.now());
				csvFile.addLine(i, msIntervals, test);
				System.out.println(i + ": " + msIntervals);
			}
		}

		treeNodeProcessor.destroyState();
	}

}
