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
class ModelSplitTransformerTest {

	@Mock
	private ModelSplitter modelSplitter;

	@Test
	void testApply() {
		String subjectType = "subjectType";
		ModelSplitTransformer modelSplitTransformer = new ModelSplitTransformer(subjectType, modelSplitter);
		Model modelA = ModelFactory.createDefaultModel();
		Model modelB = ModelFactory.createDefaultModel();
		Model modelC = ModelFactory.createDefaultModel();
		when(modelSplitter.split(modelA, subjectType)).thenReturn(Set.of(modelB, modelC));

		List<Model> result = modelSplitTransformer.apply(modelA).stream().toList();

		assertEquals(2, result.size());
		assertFalse(result.contains(modelA));
		assertTrue(result.contains(modelB));
		assertTrue(result.contains(modelC));
	}

}