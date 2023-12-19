package ldes.client.startingtreenode;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.startingtreenode.domain.valueobjects.RedirectHistory;
import ldes.client.startingtreenode.domain.valueobjects.StartingNodeRequest;
import ldes.client.startingtreenode.domain.valueobjects.StartingTreeNode;
import ldes.client.startingtreenode.exception.StartingNodeNotFoundException;
import org.apache.jena.riot.RDFLanguages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StartingTreeNodeFinderSteps {

	private final RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory();
	private StartingTreeNodeFinder startingTreeNodeFinder;
	private StartingNodeRequest startingNodeRequest;

	@Given("I have a StartingTreeNodeFinder$")
	public void initializeCalculator() {
		startingTreeNodeFinder = new StartingTreeNodeFinder(requestExecutorFactory.createNoAuthExecutor());
	}

	@Then("the starting Tree Node of the LDES Stream is the url of the View: {string}")
	public void theStartingTreeNodeOfTheLDESStreamIsTheUrlOfTheView(String url) {
		StartingTreeNode startingTreeNode = startingTreeNodeFinder.determineStartingTreeNode(startingNodeRequest);
		assertEquals(url, startingTreeNode.getUrl());
	}

	@When("I create a StartingNodeRequest with a lang {string} and url: {string}")
	public void iProvideTheEndpointOfATreeNodeThatIsAlsoAView(String lang, String url) {
		startingNodeRequest = new StartingNodeRequest(url, RDFLanguages.nameToLang(lang), new RedirectHistory());
	}

	@Then("An StartingNodeNotFoundException is Thrown indicating that I'm in an infinite loop")
	public void anStartingNodeNotFoundExceptionIsThrownIndicatingThatIMInAnInfiteLoop() {
		StartingNodeNotFoundException startingNodeNotFoundException = assertThrows(StartingNodeNotFoundException.class,
				() -> startingTreeNodeFinder.determineStartingTreeNode(startingNodeRequest));
		assertEquals(
				"Starting Node could not be identified from url http://localhost:10101/302-redirects-infinite-loop-1.\n"
						+
						"Infinite redirect loop.",
				startingNodeNotFoundException.getMessage());
	}

	@Then("An StartingNodeNotFoundException is Thrown indicating that status {int} cannot be handled")
	public void anStartingNodeNotFoundExceptionIsThrownIndicatingThatIMInAnInfiteLoop(int status) {
		StartingNodeNotFoundException startingNodeNotFoundException = assertThrows(StartingNodeNotFoundException.class,
				() -> startingTreeNodeFinder.determineStartingTreeNode(startingNodeRequest));
		assertEquals("Starting Node could not be identified from url http://localhost:10101/303-unsupported-status.\n" +
				"Unable to hande response " + status,
				startingNodeNotFoundException.getMessage());
	}

}
