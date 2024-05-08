package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChangeDetectionFilterTest {
	private ChangeDetectionFilter changeDetectionFilter = new ChangeDetectionFilter();

	@Test
	void when_FilterValidStateObject_then_ThrowNoException() {
		final Model validStateObject = supplyStateObject();

		assertThatNoException().isThrownBy(() -> changeDetectionFilter.transform(validStateObject));
	}

	@ParameterizedTest
	@MethodSource("supplyInvalidTriples")
	void when_FilterInvalidStateObjects_then_ThrowException(String triples) {
		final Model invalidStateObject = RDFParser.fromString(triples).lang(Lang.NT).toModel();

		assertThatThrownBy(() -> changeDetectionFilter.transform(invalidStateObject))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("State object must contain exactly one named node");
	}

	private static Stream<Arguments> supplyInvalidTriples() {
		final String twoStateObjects = """
				<http://test-data/mobility-hindrance/1/2> <http://purl.org/dc/terms/isVersionOf> <http://test-data/mobility-hindrance/1> .
				<http://test-data/mobility-hindrance/1/2> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder> .
				<http://test-data/mobility-hindrance/1/2> <http://purl.org/dc/terms/created> "2023-04-06T09:58:15.867Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
				<http://test-data/mobility-hindrance/1/3> <http://purl.org/dc/terms/isVersionOf> <http://test-data/mobility-hindrance/1> .
				<http://test-data/mobility-hindrance/1/3> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder> .
				<http://test-data/mobility-hindrance/1/3> <http://purl.org/dc/terms/created> "2023-04-06T09:58:15.867Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
				""";
		return Stream.of(
				Arguments.of(""),
				Arguments.of(twoStateObjects)
		);
	}

	private static Model supplyStateObject() {
		final String triples = """
				@prefix dc: <http://purl.org/dc/terms/> .
				@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
				@prefix example: <http://example.org/> .

				<http://test-data/mobility-hindrance/1>
					a <https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder> ;
					dc:created "2023-04-06T09:58:15.867Z"^^xsd:dateTime ;
					example:address [
						example:city "Gent" ;
						example:street "Ottergemsesteenweg" ;
						example:houseNumber 456 ;
						example:postalCode 9000 ;
						example:country "Belgium" ;
					] .
				""";

		return RDFParser.fromString(triples).lang(Lang.TTL).toModel();
	}
}