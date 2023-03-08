package ldes.client.treenodefetcher;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.requestexecutor.domain.valueobjects.DefaultConfig;
import ldes.client.treenodefetcher.domain.entities.TreeNode;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;
import org.apache.jena.riot.RDFLanguages;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TreeNodeFetcherSteps {

	private TreeNodeFetcher treeNodeFetcher;
	private TreeNodeRequest treeNodeRequest;
	private TreeNode treeNode;

	@Given("I have a TreeNodeFetcher")
	public void initializeCalculator() {
		treeNodeFetcher = new TreeNodeFetcher(new DefaultConfig().createRequestExecutor());
	}

	@When("I create a TreeNodeRequest with Lang {string} and url {string}")
	public void iCreateATreeNodeRequestWithLangAndUrl(String arg0, String arg1) {
		treeNodeRequest = new TreeNodeRequest(arg1, RDFLanguages.nameToLang(arg0));
	}

	@And("I fetch the TreeNode")
	public void iFetchTheTreeNode() {
		treeNode = treeNodeFetcher.fetchTreeNode(treeNodeRequest);
	}

	@Then("the obtained TreeNode has {int} members and {int} relations")
	public void theObtainedTreeNodeHasMembersAndRelations(int arg0, int arg1) {
		assertEquals(arg0, treeNode.getMembers().size());
		assertEquals(arg1, treeNode.getRelations().size());
	}
}
