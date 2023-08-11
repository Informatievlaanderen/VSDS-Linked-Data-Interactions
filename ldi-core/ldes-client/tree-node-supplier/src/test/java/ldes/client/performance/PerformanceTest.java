package ldes.client.performance;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import ldes.client.performance.csvwriter.CsvFile;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.apache.commons.io.FilenameUtils.separatorsToSystem;

// TODO: 11/08/23 run against 8080
// TODO: 11/08/23 run against real postgres
public class PerformanceTest {

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

	@Tag("performance")
	@Test
	void compare_persistence_strategies_f10_s1000() {
		testRunner(
				separatorsToSystem("target/compare_persistence_strategies_f10_s1000.csv"),
				1000,
				List.of(TestScenario.FILE10, TestScenario.MEMORY10, TestScenario.SQLITE10, TestScenario.POSTGRES10));
	}

	@Tag("performance")
	@Test
	void compare_persistence_strategies_f250_s1000() {
		testRunner(
				separatorsToSystem("target/compare_persistence_strategies_f250_s1000.csv"),
				1000,
				List.of(TestScenario.FILE250, TestScenario.MEMORY250, TestScenario.SQLITE250, TestScenario.POSTGRES250));
	}

	@Tag("performance")
	@Test
	void compare_persistence_strategies_f10_s100_000() {
		testRunner(
				separatorsToSystem("target/compare_persistence_strategies_f10_s100_000.csv"),
				100_000,
				List.of(TestScenario.FILE10, TestScenario.MEMORY10, TestScenario.SQLITE10, TestScenario.POSTGRES10));
	}

	@Tag("performance")
	@Test
	void test_memory_f250_s100_000() {
		testRunner(
				separatorsToSystem("target/test_memory_f250_s100_000.csv"),
				100_000,
				List.of(TestScenario.MEMORY250));
	}

	// Runs 1h14m
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
				.createTreeNodeProcessor(test.getPersistenceStrategy(), test.getStartingEndpoint());

		LocalDateTime lastInterval = LocalDateTime.now();
		for (int i = 1; i <= testSize; i++) {
			treeNodeProcessor.getMember();
			if (i % (testSize / 20) == 0) {
				int msIntervals = (int) ChronoUnit.MILLIS.between(lastInterval, lastInterval = LocalDateTime.now());
				csvFile.addLine(i, msIntervals, test);
				System.out.println(i + ": " + msIntervals);
			}
		}
	}

}
