package ldes.client.startingtreenode;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.startingtreenode.domain.valueobjects.RedirectHistory;
import ldes.client.startingtreenode.domain.valueobjects.StartingNodeRequest;
import ldes.client.startingtreenode.domain.valueobjects.StartingTreeNode;
import org.apache.jena.riot.RDFLanguages;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StartingTreeNodeRelationsFinderSteps {
	private StartingTreeNodeRelationsFinder startingTreeNodeRelationsFinder;
	private StartingNodeRequest startingNodeRequest;
	private List<StartingTreeNode> startingNodes;

	@Given("I have a StartingTreeNodeRelationsFinder")
	public void iHaveAStartingTreeNodeRelationsFinder() {
		final RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory();
		final RequestExecutor requestExecutor = requestExecutorFactory.createNoAuthExecutor();
		startingTreeNodeRelationsFinder = new StartingTreeNodeRelationsFinder(requestExecutor);
	}

	@And("I have a StartingNodeRequest with a lang {string} and url: {string}")
	public void iHaveAStartingNodeRequestWithALangAndUrl(String lang, String url) {
		startingNodeRequest = new StartingNodeRequest(url, RDFLanguages.nameToLang(lang), new RedirectHistory());
	}

	@When("I execute the StartingNodeRequest")
	public void iExecuteTheStartingNodeRequest() {
		startingNodes = startingTreeNodeRelationsFinder.findAllStartingTreeNodes(startingNodeRequest);
	}

	@Then("the starting nodes contains following starting uris")
	public void theStartingNodesContains(List<String> startingUris) {
		assertThat(startingNodes)
				.map(StartingTreeNode::getUrl)
				.containsExactlyInAnyOrderElementsOf(startingUris);
	}
}
