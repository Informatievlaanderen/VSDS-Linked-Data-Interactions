package ldes.client.treenoderelationsfetcher;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.LdesRelation;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.LdesStructure;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest(httpPort = LdesStructureDiscovererTest.WIREMOCK_PORT)
class LdesStructureDiscovererTest {
	protected static final int WIREMOCK_PORT = 10203;
	private static final String STARTING_ENDPOINT = "/ldes";
	private static final String CONTENT_TYPE = "text/turtle";
	private LdesStructureDiscoverer discoverer;

	@BeforeEach
	void setUp() {
		final RequestExecutor requestExecutor = new RequestExecutorFactory().createNoAuthExecutor();
		discoverer = new LdesStructureDiscoverer("http://localhost:%d%s".formatted(WIREMOCK_PORT, STARTING_ENDPOINT), Lang.TURTLE, requestExecutor);
	}

	@Test
	void when_DiscoverRelations_then_ReturnListOfThreeRelations() throws IOException {
		final List<String> expectedChildRelations = Stream.of("/by-page", "/by-time")
				.map(endpoint -> "http://localhost:%d%s%s".formatted(WIREMOCK_PORT, STARTING_ENDPOINT, endpoint))
				.toList();

		final List<String> expectedGrandChildRelations = Stream.of("/by-page?pageNumber=1", "/by-time?year=2023")
				.map(endpoint -> "http://localhost:%d%s%s".formatted(WIREMOCK_PORT, STARTING_ENDPOINT, endpoint))
				.toList();

		final String ldesBody = readDataFromFile("tree-nodes/ldes.ttl");
		stubFor(get("/ldes").willReturn(okForContentType(CONTENT_TYPE, ldesBody)));

		final String byPageBody = readDataFromFile("tree-nodes/by-page.ttl");
		stubFor(get("/ldes/by-page").willReturn(okForContentType(CONTENT_TYPE, byPageBody)));

		final String byTimeBody = readDataFromFile("tree-nodes/by-time.ttl");
		stubFor(get("/ldes/by-time").willReturn(okForContentType(CONTENT_TYPE, byTimeBody)));

		final String byTimeYear2023Body = readDataFromFile("tree-nodes/by-time-2023.ttl");
		stubFor(get("/ldes/by-time?year=2023").willReturn(okForContentType(CONTENT_TYPE, byTimeYear2023Body)));

		final String byTimeYear2023Month05Body = readDataFromFile("tree-nodes/by-time-2023-05.ttl");
		stubFor(get("/ldes/by-time?year=2023&month=05").willReturn(okForContentType(CONTENT_TYPE, byTimeYear2023Month05Body)));

		stubFor(get("/302-redirects").willReturn(status(302).withHeader("Location", "http://localhost:10101/404-not-found")));

		LdesStructure ldesStructure = discoverer.discoverLdesStructure();

		assertThat(ldesStructure.countTotalRelations()).isEqualTo(5);
		assertThat(ldesStructure.getRelations())
				.map(LdesRelation::getUri)
				.containsExactlyInAnyOrderElementsOf(expectedChildRelations);
		assertThat(ldesStructure.getRelations())
				.flatMap(relation -> relation.getRelations().stream().map(LdesRelation::getUri).toList())
				.containsExactlyInAnyOrderElementsOf(expectedGrandChildRelations);
	}

	private static String readDataFromFile(String filename) throws IOException {
		final ClassLoader classLoader = LdesStructureDiscovererTest.class.getClassLoader();
		final File file = new File(Objects.requireNonNull(classLoader.getResource(filename)).getFile());
		return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
	}
}
