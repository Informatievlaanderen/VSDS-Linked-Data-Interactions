package be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.startingnode;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AbstractStartingNodeSupplierTest {

	private Model model;

	@BeforeEach
	void setUp() {
		model = ModelFactory.createDefaultModel();
	}

	@Test
	void shouldReturnEmpty_whenThereIsNoNextHandler() {
		AbstractStartingNodeSupplier supplierA = new AbstractStartingNodeSupplier(null) {
		};
		assertTrue(supplierA.getStartingNode(model).isEmpty());
	}

	@Test
	void shouldReturnValueFromNextHandler_whenThereIsOne() {
		AbstractStartingNodeSupplier supplierB = mock(AbstractStartingNodeSupplier.class);
		AbstractStartingNodeSupplier supplierA = new AbstractStartingNodeSupplier(supplierB) {
		};
		when(supplierB.getStartingNode(model)).thenReturn(Optional.of(new StartingNode("node B")));

		Optional<StartingNode> resultNode = supplierA.getStartingNode(model);
		assertTrue(resultNode.isPresent());
		assertEquals("node B", resultNode.get().url());
	}

}