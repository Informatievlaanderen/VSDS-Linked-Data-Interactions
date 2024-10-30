package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.SkolemisationTransformer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.util.SkolemizationConditions.*;
import static org.assertj.core.api.Assertions.assertThat;

class SkolemizedModelTest {
	private static final String SKOLEM_URI_TEMPLATE = "http://example.org" + SkolemisationTransformer.SKOLEM_URI + "%s";

	@Test
	void given_ModelWithBNodes_when_getModel_then_NoBNodesArePresent() {
		final Model modelToSkolemize = RDFParser.source("activity.nq").toModel();

		final Model skolemizedModel = new SkolemizedModel(SKOLEM_URI_TEMPLATE, modelToSkolemize).getModel();

		assertThat(skolemizedModel)
				.has(noBlankNodes())
				.has(skolemizedObjectsWithPrefix(1, SKOLEM_URI_TEMPLATE.replace("%s", "")))
				.has(skolemizedSubjectsWithPrefix(2, SKOLEM_URI_TEMPLATE.replace("%s", "")));
	}

	@Test
	void given_ModelWithoutBNodes_when_getModel_then_ThereAreStillNoBNodesPresent() {
		final Model modelToSkolemize = RDFParser.source("skolemized-product.nq").toModel();

		final Model skolemizedModel = new SkolemizedModel(SKOLEM_URI_TEMPLATE, modelToSkolemize).getModel();

		assertThat(skolemizedModel).has(noBlankNodes());
	}
}