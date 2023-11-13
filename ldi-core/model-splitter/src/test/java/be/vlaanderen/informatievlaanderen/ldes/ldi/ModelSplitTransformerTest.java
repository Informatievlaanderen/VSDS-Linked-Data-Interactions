package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ModelSplitTransformerTest {

	@Test
	void testApply() {
		String subjectType = "http://schema.org/Movie";
		ModelSplitTransformer modelSplitTransformer = new ModelSplitTransformer(subjectType);

		Model inputModel = RDFParser.source("generic/input.ttl").toModel();

		List<Model> result = modelSplitTransformer.transform(inputModel).stream().toList();

		assertEquals(2, result.size());
	}

}
