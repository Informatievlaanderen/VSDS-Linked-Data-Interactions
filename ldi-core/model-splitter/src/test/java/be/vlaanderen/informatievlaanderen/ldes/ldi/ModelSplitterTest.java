package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelSplitterTest {

	@Test
	void test_generic() {
		Model inputModel = RDFParser.source("generic/input.ttl").toModel();
		Set<Model> result = new ModelSplitter().split(inputModel, "http://schema.org/Movie");

		assertEquals(2, result.size());
		assertThing(List.of("generic/member1.ttl", "generic/member2.ttl"), result);
	}

	@Test
	void test_crowdscan() {
		Model inputModel = RDFParser.source("crowdscan/input.ttl").toModel();
		Set<Model> result = new ModelSplitter().split(inputModel,
				"http://def.isotc211.org/iso19156/2011/Observation#OM_Observation");

		assertEquals(3, result.size());
		assertThing(List.of("crowdscan/observation1.ttl", "crowdscan/observation2.ttl", "crowdscan/observation3.ttl"),
				result);
	}

	@Test
	void test_traffic() {
		Model inputModel = RDFParser.source("traffic/input.ttl").toModel();
		Set<Model> result = new ModelSplitter().split(inputModel,
				"https://data.vlaanderen.be/ns/verkeersmetingen#Verkeersmeting");

		assertEquals(10, result.size());
		assertThing(List.of(
				"traffic/measure1.ttl",
				"traffic/measure2.ttl",
				"traffic/measure3.ttl",
				"traffic/measure4.ttl",
				"traffic/measure5.ttl",
				"traffic/measure6.ttl",
				"traffic/measure7.ttl",
				"traffic/measure8.ttl",
				"traffic/measure9.ttl",
				"traffic/measure10.ttl"), result);
	}

	private void assertThing(List<String> expectedModelPaths, Set<Model> result) {
		Set<Model> expectedModels = expectedModelPaths
				.stream()
				.map(RDFParser::source)
				.map(RDFParserBuilder::toModel)
				.collect(Collectors.toSet());

		result.forEach(
				actualResult -> expectedModels
						.removeIf(expectedResult -> expectedResult.isIsomorphicWith(actualResult)));

		assertTrue(expectedModels.isEmpty());
	}

}
