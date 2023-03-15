package ldes.client.startingtreenode;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.requestexecutor.domain.valueobjects.executorsupplier.DefaultConfig;
import ldes.client.startingtreenode.domain.valueobjects.StartingNodeRequest;
import ldes.client.startingtreenode.domain.valueobjects.StartingTreeNode;
import org.apache.jena.riot.Lang;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StartingTreeNodeFinderSteps {

	private StartingTreeNode treeNode;

	private StartingTreeNodeFinder calculator;

	@Given("I have a StartingTreeNodeFinder$")
	public void initializeCalculator() {
		calculator = new StartingTreeNodeFinder(new DefaultConfig().createRequestExecutor());
	}

	@Then("the starting Tree Node of the LDES Stream is the url of the View: {string}")
	public void theStartingTreeNodeOfTheLDESStreamIsTheUrlOfTheView(String url) {
		assertEquals(new StartingTreeNode(url), treeNode);
	}

	@When("I provide the endpoint of a Tree Node that is also a View: {string}")
	public void iProvideTheEndpointOfATreeNodeThatIsAlsoAView(String url) {
		treeNode = calculator.determineStartingTreeNode(new StartingNodeRequest(url, Lang.JSONLD));
	}

	@When("I provide an endpoint that redirects to a Tree Node that is also a View: {string}")
	public void iProvideAnEndpointThatRedirectsToATreeNodeThatIsAlsoAView(String url) {
		treeNode = calculator.determineStartingTreeNode(new StartingNodeRequest(url, Lang.JSONLD));
	}
}
