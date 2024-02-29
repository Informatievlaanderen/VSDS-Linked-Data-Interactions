package ldes.client.treenoderelationsfetcher;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.LdesStructure;
import org.apache.jena.riot.Lang;

import static org.assertj.core.api.Assertions.assertThat;

public class LdesStructureDiscovererSteps {
	private LdesStructureDiscoverer ldesStructureDiscoverer;
	private LdesStructure ldesStructure;

	@Given("I have a LdesStructureDiscoverer")
	public void iHaveATreeDiscoverer() {
		final String startingUrl = "http://localhost:10101/200-first-4-relations";
		final Lang sourceFormat = Lang.TURTLE;
		ldesStructureDiscoverer = new LdesStructureDiscoverer(startingUrl, sourceFormat, new RequestExecutorFactory().createNoAuthExecutor());
	}

	@When("I start discovering the tree node relations")
	public void iStartDiscoveringTheTreeNodeRelations() {
		ldesStructure = ldesStructureDiscoverer.discoverLdesStructure();
	}

	@Then("I got a {int} relations in total")
	public void iGotARelations(int numberOfRelations) {
		assertThat(ldesStructure.countTotalRelations()).isEqualTo(numberOfRelations);
	}

	@Then("I got {int} child relations")
	public void iGotChildRelations(int numberOfChildRelations) {
		assertThat(ldesStructure.countChildRelations()).isEqualTo(numberOfChildRelations);
	}
}
