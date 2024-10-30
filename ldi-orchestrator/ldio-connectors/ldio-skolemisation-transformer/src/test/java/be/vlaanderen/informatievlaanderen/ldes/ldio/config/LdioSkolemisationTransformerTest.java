package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.SkolemisationTransformer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LdioSkolemisationTransformerTest {
	@Mock
	private SkolemisationTransformer skolemisationTransformer;
	@InjectMocks
	private LdioSkolemisationTransformer ldioSkolemisationTransformer;

	@Test
	void test_Apply() {
		Model model = ModelFactory.createDefaultModel();
		ldioSkolemisationTransformer.apply(model);

		verify(skolemisationTransformer).transform(model);
	}
}