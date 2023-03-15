package ldes.client.treenodefetcher;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.requestexecutor.domain.valueobjects.executorsupplier.DefaultConfig;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeResponse;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;
import org.apache.jena.riot.RDFLanguages;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TreeNodeFetcherSteps {

	private TreeNodeFetcher treeNodeFetcher;
	private TreeNodeRequest treeNodeRequest;
	private TreeNodeResponse treeNodeResponse;

	@Given("I have a TreeNodeFetcher")
	public void initializeCalculator() {
		treeNodeFetcher = new TreeNodeFetcher(new DefaultConfig().createRequestExecutor());
	}

	@When("I create a TreeNodeRequest with Lang {string} and url {string}")
	public void iCreateATreeNodeRequestWithLangAndUrl(String lang, String url) {
		treeNodeRequest = new TreeNodeRequest(url, RDFLanguages.nameToLang(lang));
	}

	@And("I fetch the TreeNode")
	public void iFetchTheTreeNode() {
		treeNodeResponse = treeNodeFetcher.fetchTreeNode(treeNodeRequest);
	}

	@Then("the obtained TreeNode has {int} members and {int} relations")
	public void theObtainedTreeNodeHasMembersAndRelations(int numberOfMembers, int numberOfRelations) {
		assertEquals(numberOfMembers, treeNodeResponse.getMembers().size());
		assertEquals(numberOfRelations, treeNodeResponse.getRelations().size());
	}
}
