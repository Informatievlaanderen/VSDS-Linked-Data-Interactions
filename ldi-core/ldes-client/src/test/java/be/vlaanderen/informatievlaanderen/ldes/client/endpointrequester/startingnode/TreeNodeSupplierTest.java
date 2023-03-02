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

class TreeNodeSupplierTest {

	private Model model;
	private String id;
	private TreeNodeSupplier treeNodeSupplier;

	@BeforeEach
	void setUp() {
		model = ModelFactory.createDefaultModel();
		id = "http://localhost:8080/mobility-hindrances";
		treeNodeSupplier = new TreeNodeSupplier(null);
	}

	@Test
	void whenHasTreeNode_shouldReturnSubjectFromTreeNode() {
		model.add(createResource(id), RDF_SYNTAX_TYPE, TREE_NODE_RESOURCE);
		model.add(createResource(id + "/view"), RDF_SYNTAX_TYPE, TREE_VIEW);

		Optional<StartingNode> result = treeNodeSupplier.getStartingNode(model);

		assertTrue(result.isPresent());
		assertEquals(id, result.get().url());
	}

	@Test
	void whenHasNoTreeNode_shouldReturnEmpty() {
		model.add(createResource(id), TREE_VIEW, createResource(id + "/view1"));
		model.add(createResource(id), TREE_VIEW, createResource(id + "/view2"));

		Optional<StartingNode> result = treeNodeSupplier.getStartingNode(model);

		assertTrue(result.isEmpty());
	}

}