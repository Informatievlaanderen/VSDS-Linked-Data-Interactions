package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.common;

import be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.config.LdesDiscovererConfig;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.TreeNodeRelation;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

class LdesDiscovererTest {
	private LdesDiscoverer ldesDiscoverer;

	@BeforeEach
	void setUp() {
		final LdesDiscovererConfig ldesDiscovererConfig = new LdesDiscovererConfig();
		ldesDiscovererConfig.setUrl("http://localhost:10101/200-first-2-relations");
		ldesDiscovererConfig.setSourceFormat("text/turtle");
		ldesDiscoverer = new LdesDiscoverer(ldesDiscovererConfig);
	}


	@Nested
	@WireMockTest(httpPort = WireMockTests.WIREMOCK_PORT)
	class WireMockTests {
		private static final int WIREMOCK_PORT = 10101;
		private static final String CONTENT_TYPE = "text/turtle";

		@Test
		void when_DiscoverRelations_then_ReturnListOfThreeRelations() throws IOException {
			String bodyFirstResponse = readDataFromFile("tree-nodes/3-relations.ttl");
			stubFor(get("/200-first-2-relations").willReturn(okForContentType(CONTENT_TYPE, bodyFirstResponse)));
			String bodySecondResponse = readDataFromFile("tree-nodes/1-relation.ttl");
			stubFor(get("/200-first-relation").willReturn(okForContentType(CONTENT_TYPE, bodySecondResponse)));
			stubFor(get("/200-no-relations").willReturn(okForContentType(CONTENT_TYPE, "")));
			stubFor(get("/200-only-members").willReturn(okForContentType(CONTENT_TYPE, "")));
			stubFor(get("/302-redirects").willReturn(status(302).withHeader("Location", "http://localhost:10101/404-not-found")));

			List<TreeNodeRelation> treeNodeRelations = ldesDiscoverer.discoverRelations();

			assertThat(treeNodeRelations)
					.hasSize(4)
					.containsExactlyInAnyOrderElementsOf(readTreeRelations());
			verify(getRequestedFor(urlEqualTo("/200-first-2-relations")));
		}

	}


	@Test
	void given_ListOfTreeRelations_when_BuildLdesStructure_then_ReturnModel() {
		final List<TreeNodeRelation> treeNodeRelations = readTreeRelations();
		Model expectedLdesStructure = RDFDataMgr.loadModel("ldes-structure.ttl");

		Model actualLdesStructure = ldesDiscoverer.buildLdesStructure(treeNodeRelations);

		assertThat(actualLdesStructure)
				.matches(expectedLdesStructure::isIsomorphicWith);
	}


	private static String readDataFromFile(String filename) throws IOException {
		File path = ResourceUtils.getFile("classpath:" + filename);
		return FileUtils.readFileToString(path, StandardCharsets.UTF_8);
	}

	private static List<TreeNodeRelation> readTreeRelations() {
		return Stream.of("relation-1.ttl", "relation-2.ttl", "relation-3.ttl", "relation-4.ttl")
				.map("tree-relations/%s"::formatted)
				.map(RDFDataMgr::loadModel)
				.map(TreeNodeRelation::fromModel)
				.toList();
	}


}
