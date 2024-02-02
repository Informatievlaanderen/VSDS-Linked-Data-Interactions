package ldes.client.treenodesupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRelation;
import ldes.client.treenodesupplier.domain.valueobject.LdesMetaData;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TreeDiscovererSteps {
	private TreeNodeDiscoverer treeNodeDiscoverer;
	private List<TreeNodeRelation> treeNodeRelations;

	@Given("I have a TreeDiscoverer")
	public void iHaveATreeDiscoverer() {
		final LdesMetaData ldesMetaData = new LdesMetaData("http://localhost:10101/200-first-2-relations", Lang.TURTLE);
		treeNodeDiscoverer = new TreeNodeDiscoverer(ldesMetaData, new RequestExecutorFactory().createNoAuthExecutor());
	}

	@When("I start")
	public void iStart() {
		treeNodeDiscoverer.discoverNodes();
	}

	@When("I start discovering the tree node relations")
	public void iStartDiscoveringTheTreeNodeRelations() {
		treeNodeRelations = treeNodeDiscoverer.discoverNodes();
	}

	@Then("I got a {int} relations")
	public void iGotARelations(int numberOfRelations) {
		assertThat(treeNodeRelations).hasSize(numberOfRelations);
	}

	@And("The structure is isomorphic with the expected model")
	public void theStructureIsIsomorphicWithTheExpectedModel() {
		final Model expectedStructure = RDFDataMgr.loadModel("__files/ldes-structure.ttl");
		final Model actualStructure = ModelFactory.createDefaultModel();
		treeNodeRelations.stream().map(TreeNodeRelation::getRelationModel).forEach(actualStructure::add);
		assertThat(actualStructure).matches(expectedStructure::isIsomorphicWith);
	}
}
