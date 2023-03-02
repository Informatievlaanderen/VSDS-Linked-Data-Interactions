package be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.startingnode;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.startingnode.TreeNodeSupplier.RDF_SYNTAX_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.startingnode.TreeNodeSupplier.TREE_NODE_RESOURCE;
import static be.vlaanderen.informatievlaanderen.ldes.client.endpointrequester.startingnode.ViewSupplier.TREE_VIEW;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewSupplierTest {

	private Model model;
	private String id;
	private ViewSupplier viewSupplier;

	@BeforeEach
	void setUp() {
		model = ModelFactory.createDefaultModel();
		id = "http://localhost:8080/mobility-hindrances";
		viewSupplier = new ViewSupplier(null);
	}

	@Test
	void whenHasViewStatement_shouldReturnObjectOfViewNode() {
		model.add(createResource(id), RDF_SYNTAX_TYPE, TREE_NODE_RESOURCE);
		model.add(createResource(id), TREE_VIEW, createResource(id + "/view1"));

		Optional<StartingNode> result = viewSupplier.getStartingNode(model);

		assertTrue(result.isPresent());
		assertEquals(id + "/view1", result.get().url());
	}

	@Test
	void whenHasMultipleViewStatements_shouldReturnObjectOfAnyViewStatement() {
		model.add(createResource(id), RDF_SYNTAX_TYPE, TREE_NODE_RESOURCE);
		model.add(createResource(id), TREE_VIEW, createResource(id + "/view1"));
		model.add(createResource(id), TREE_VIEW, createResource(id + "/view2"));

		Optional<StartingNode> result = viewSupplier.getStartingNode(model);

		assertTrue(result.isPresent());
		assertTrue(result.get().url().contains("view"));
	}

	@Test
	void whenHasNoViewStatement_shouldReturnEmpty() {
		model.add(createResource(id), RDF_SYNTAX_TYPE, TREE_NODE_RESOURCE);

		Optional<StartingNode> result = viewSupplier.getStartingNode(model);

		assertTrue(result.isEmpty());
	}

}