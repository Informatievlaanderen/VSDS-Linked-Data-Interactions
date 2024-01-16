package ldes.client.treenodefetcher;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampFromCurrentTimeExtractor;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeResponse;
import org.apache.jena.riot.RDFLanguages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TreeNodeFetcherSteps {

	private final RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory();
	private TreeNodeFetcher treeNodeFetcher;
	private TreeNodeRequest treeNodeRequest;
	private TreeNodeResponse treeNodeResponse;

	@Given("I have a TreeNodeFetcher")
	public void initializeCalculator() {
		treeNodeFetcher = new TreeNodeFetcher(requestExecutorFactory.createNoAuthExecutor(), new TimestampFromCurrentTimeExtractor());
	}

	@When("I create a TreeNodeRequest with Lang {string} and url {string}")
	public void iCreateATreeNodeRequestWithLangAndUrl(String lang, String url) {
		treeNodeRequest = new TreeNodeRequest(url, RDFLanguages.nameToLang(lang), null);
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

	@When("I create a TreeNodeRequest with Lang {string} and url {string} and etag {string}")
	public void iCreateATreeNodeRequestWithLangAndUrlAndEtag(String lang, String url, String etag) {
		treeNodeRequest = new TreeNodeRequest(url, RDFLanguages.nameToLang(lang), etag.isEmpty() ? null : etag);
	}

	@Then("An UnSupportedOperationException is thrown")
	public void anUnSupportedOperationExceptionIsThrown() {
		UnsupportedOperationException unsupportedOperationException = assertThrows(UnsupportedOperationException.class,
				() -> treeNodeFetcher.fetchTreeNode(treeNodeRequest));
		assertEquals(
				"Cannot handle response 404 of TreeNodeRequest TreeNodeRequest{treeNodeUrl='http://localhost:10101/404-not-found', lang=Lang:Turtle, etag='null'}",
				unsupportedOperationException.getMessage());
	}
}
