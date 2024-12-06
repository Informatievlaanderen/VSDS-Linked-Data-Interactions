package be.vlaanderen.informatievlaanderen.ldes.ldi.skolemisation;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmptySkolemizerTest {
	private final Skolemizer skolemizer = new EmptySkolemizer();
	@Test
	void test_Skolemize() {
		final Model model = ModelFactory.createDefaultModel();

		final Model result = skolemizer.skolemize(model);

		assertThat(result).isSameAs(model);
	}
}