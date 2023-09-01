package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModelSplitAdapterTest {

	@Mock
	private LdiAdapter ldiAdapter;

	@Mock
	private ModelSplitter modelSplitter;

	@Test
	void testApply() {
		String subjectType = "subjectType";
		ModelSplitAdapter modelSplitAdapter = new ModelSplitAdapter(subjectType, ldiAdapter, modelSplitter);
		LdiAdapter.Content content = LdiAdapter.Content.of("content", "mime");
		Model modelA = ModelFactory.createDefaultModel();
		when(ldiAdapter.apply(content)).thenReturn(Stream.of(modelA));
		Model modelB = ModelFactory.createDefaultModel();
		Model modelC = ModelFactory.createDefaultModel();
		when(modelSplitter.split(modelA, subjectType)).thenReturn(Set.of(modelB, modelC));

		List<Model> result = modelSplitAdapter.apply(content).toList();

		assertEquals(2, result.size());
		assertFalse(result.contains(modelA));
		assertTrue(result.contains(modelB));
		assertTrue(result.contains(modelC));
	}

}