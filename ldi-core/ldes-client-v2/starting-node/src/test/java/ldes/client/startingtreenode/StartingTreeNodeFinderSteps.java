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
import org.apache.jena.riot.RDFLanguages;

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
						"Infite redirect loop.",
				startingNodeNotFoundException.getMessage());
	}

}
