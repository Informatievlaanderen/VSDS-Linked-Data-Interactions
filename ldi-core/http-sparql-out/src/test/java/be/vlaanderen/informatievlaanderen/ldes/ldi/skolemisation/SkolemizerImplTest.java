package be.vlaanderen.informatievlaanderen.ldes.ldi.skolemisation;

import be.vlaanderen.informatievlaanderen.ldes.ldi.SkolemisationTransformer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkolemizerImplTest {
	@Mock
	private SkolemisationTransformer skolemisationTransformer;
	@InjectMocks
	private SkolemizerImpl skolemizer;

	@Test
	void test_Skolemize() {
		final Model input = ModelFactory.createDefaultModel();
		final Model output = ModelFactory.createDefaultModel();
		when(skolemisationTransformer.transform(input)).thenReturn(output);

		final Model result = skolemizer.skolemize(input);

		assertThat(result)
				.isSameAs(output)
				.isNotSameAs(input);
	}
}