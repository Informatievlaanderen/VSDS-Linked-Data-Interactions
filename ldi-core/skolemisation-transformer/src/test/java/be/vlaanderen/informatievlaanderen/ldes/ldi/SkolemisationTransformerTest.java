package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.util.SkolemizationConditions.*;
import static org.assertj.core.api.Assertions.assertThat;

class SkolemisationTransformerTest {
	private static final String SKOLEMIZATION_DOMAIN = "http://example.com";
	private SkolemisationTransformer skolemisationTransformer;

	@BeforeEach
	void setUp() {
		skolemisationTransformer = new SkolemisationTransformer(SKOLEMIZATION_DOMAIN);
	}

	@Test
	void given_ModelWithBNodes_when_Transform_then_ReturnModelWithoutBNodes() {
		final Model input = RDFParser.source("mob-hind-model.ttl").toModel();

		final Model result = skolemisationTransformer.transform(input);

		assertThat(result)
				.has(noBlankNodes())
				.has(skolemizedSubjectsWithPrefix(2, SKOLEMIZATION_DOMAIN + SkolemisationTransformer.SKOLEM_URI))
				.has(skolemizedObjectsWithPrefix(2, SKOLEMIZATION_DOMAIN + SkolemisationTransformer.SKOLEM_URI));
	}
}