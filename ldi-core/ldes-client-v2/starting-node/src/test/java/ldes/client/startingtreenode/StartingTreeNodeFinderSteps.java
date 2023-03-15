package ldes.client.startingtreenode;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.requestexecutor.domain.valueobjects.executorsupplier.DefaultConfig;
import ldes.client.startingtreenode.domain.valueobjects.RedirectHistory;
import ldes.client.startingtreenode.domain.valueobjects.StartingNodeRequest;
import ldes.client.startingtreenode.domain.valueobjects.StartingTreeNode;
import ldes.client.startingtreenode.exception.StartingNodeNotFoundException;
import org.apache.jena.riot.Lang;

import static org.junit.jupiter.api.Assertions.*;

public class StartingTreeNodeFinderSteps {

	private StartingTreeNodeFinder startingTreeNodeFinder;
	private StartingNodeRequest startingNodeRequest;

	@Given("I have a StartingTreeNodeFinder$")
	public void initializeCalculator() {
		startingTreeNodeFinder = new StartingTreeNodeFinder(new DefaultConfig().createRequestExecutor());
	}

	@Then("the starting Tree Node of the LDES Stream is the url of the View: {string}")
	public void theStartingTreeNodeOfTheLDESStreamIsTheUrlOfTheView(String url) {
		StartingTreeNode startingTreeNode = startingTreeNodeFinder.determineStartingTreeNode(startingNodeRequest);
		assertEquals(url, startingTreeNode.getUrl());
	}

	@When("I provide the endpoint of a Tree Node that is also a View: {string}")
	public void iProvideTheEndpointOfATreeNodeThatIsAlsoAView(String url) {
		startingNodeRequest = new StartingNodeRequest(url, Lang.JSONLD, new RedirectHistory());
	}

	@When("I provide an endpoint that redirects to a Tree Node that is also a View: {string}")
	public void iProvideAnEndpointThatRedirectsToATreeNodeThatIsAlsoAView(String url) {
		startingNodeRequest = new StartingNodeRequest(url, Lang.JSONLD, new RedirectHistory());
	}

	@Then("An StartingNodeNotFoundException is Thrown indicating that I'm in an infite loop;")
	public void anStartingNodeNotFoundExceptionIsThrownIndicatingThatIMInAnInfiteLoop() {
		StartingNodeNotFoundException startingNodeNotFoundException = assertThrows(StartingNodeNotFoundException.class,
				() -> startingTreeNodeFinder.determineStartingTreeNode(startingNodeRequest));
		assertEquals(
				"Starting Node could not be identified from url http://localhost:10101/302-redirects-infinite-loop-1.\n"
						+
						"Infite redirect loop.",
				startingNodeNotFoundException.getMessage());
	}

	@When("I provide an endpoint that redirects to an infinite loop: {string}")
	public void iProvideAnEndpointThatRedirectsToAnInfiniteLoop(String url) {
		startingNodeRequest = new StartingNodeRequest(url, Lang.JSONLD, new RedirectHistory());

	}
}
