package ldes.client.startingnode;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.startingtreenode.StartingTreeNodeFinder;
import ldes.client.startingtreenode.domain.valueobjects.Endpoint;
import ldes.client.startingtreenode.domain.valueobjects.TreeNode;
import org.apache.jena.riot.Lang;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StartingTreeNodeFinderSteps {

	private Optional<TreeNode> treeNode;

	private StartingTreeNodeFinder calculator;

	@Given("I have a StartingTreeNodeFinder$")
	public void initializeCalculator() {
		calculator = new StartingTreeNodeFinder();
	}

	@Then("the starting Tree Node of the LDES Stream is the url of the View: {string}")
	public void theStartingTreeNodeOfTheLDESStreamIsTheUrlOfTheView(String arg0) {
		assertTrue(treeNode.isPresent());
		assertEquals(new TreeNode(arg0), treeNode.get());
	}

	@When("I provide the endpoint of a Tree Node that is also a View: {string}")
	public void iProvideTheEndpointOfATreeNodeThatIsAlsoAView(String arg0) {
		treeNode = calculator.determineStartingTreeNode(new Endpoint(arg0, Lang.JSONLD));
	}

	@When("I provide an endpoint that redirects to a Tree Node that is also a View: {string}")
	public void iProvideAnEndpointThatRedirectsToATreeNodeThatIsAlsoAView(String arg0) {
		treeNode = calculator.determineStartingTreeNode(new Endpoint(arg0, Lang.JSONLD));
	}
}
