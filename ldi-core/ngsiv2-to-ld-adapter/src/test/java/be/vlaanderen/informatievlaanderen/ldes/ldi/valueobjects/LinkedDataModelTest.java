package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.LinkedDataModel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinkedDataModelTest {

	@Test
	void whenContextIsAlreadyPresent_thenItIsNotAddedTwice() {
		LinkedDataModel model = new LinkedDataModel();
		String context = "context";

		assertFalse(model.getContexts().contains(context));
		assertEquals(0, model.getContexts().size());

		model.addContextDeclaration(context);
		assertTrue(model.getContexts().contains(context));
		assertEquals(1, model.getContexts().size());

		model.addContextDeclaration(context);
		assertTrue(model.getContexts().contains(context));
		assertEquals(1, model.getContexts().size());
	}

	@Test
	void whenAddingMultipleContexts_thenResultingContextListIsCorrect() {
		LinkedDataModel model = new LinkedDataModel();
		List<String> contexts = new ArrayList<>();
		contexts.add("context1");
		contexts.add("context2");

		model.setContexts(contexts);
		assertTrue(model.getContexts().containsAll(contexts));
		assertEquals(2, model.getContexts().size());
	}
}
