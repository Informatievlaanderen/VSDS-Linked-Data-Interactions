package ldes.client.treenoderelationsfetcher;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.TreeNodeRelation;
import org.apache.jena.riot.Lang;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TreeDiscovererSteps {
	private TreeNodeDiscoverer treeNodeDiscoverer;
	private List<TreeNodeRelation> treeNodeRelations;

	@Given("I have a TreeDiscoverer")
	public void iHaveATreeDiscoverer() {
		final String startingUrl = "http://localhost:10101/200-first-2-relations";
		final Lang sourceFormat = Lang.TURTLE;
		treeNodeDiscoverer = new TreeNodeDiscoverer(startingUrl, sourceFormat, new RequestExecutorFactory().createNoAuthExecutor());
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
}
