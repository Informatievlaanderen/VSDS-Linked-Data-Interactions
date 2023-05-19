package ldes.client.treenodesupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.DefaultConfig;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.treenodefetcher.TreeNodeFetcher;
import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.domain.services.MemberRepositoryFactory;
import ldes.client.treenodesupplier.domain.services.TreeNodeRecordRepositoryFactory;
import ldes.client.treenodesupplier.domain.valueobject.StartingTreeNode;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistenceStrategy;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import org.apache.jena.riot.Lang;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemberSupplierSteps {

	private TreeNodeProcessor treeNodeProcessor;
	private TreeNodeRecordRepository treeNodeRecordRepository;
	private MemberRepository memberRepository;
	private MemberSupplier memberSupplier;
	private StartingTreeNode startingTreeNode;

	@When("I request the {int} members from the MemberSupplier")
	public void iRequestTheMembersFromTheMemberSupplier(int numberOfFetchedNumbers) {
		for (int i = 0; i < numberOfFetchedNumbers; i++) {
			memberSupplier.get();
		}
	}

	@When("I create a MemberSupplier")
	public void iCreateAMemberSupplier() {
		memberSupplier = new MemberSupplier(treeNodeProcessor, false);
	}

	@Then("The TreeNode is processed: {string}")
	public void theTreeNodeIsProcessed(String treeNodeId) {
		assertTrue(treeNodeRecordRepository.existsById(treeNodeId));
	}

	@Then("The TreeNode is not processed: {string}")
	public void theTreeNodeIsNotProcessed(String treeNodeId) {
		assertFalse(treeNodeRecordRepository.existsById(treeNodeId));
	}

	@Then("Status {string} for TreeNodeRecord with identifier: {string}")
	public void statusForTreeNodeRecordWithIdentifier(String treeNodeStatus, String treeNodeId) {
		assertTrue(treeNodeRecordRepository.existsByIdAndStatus(treeNodeId, TreeNodeStatus.valueOf(treeNodeStatus)));
	}

	@Given("A starting url {string}")
	public void aStartingUrl(String url) {
		startingTreeNode = new StartingTreeNodeSupplier(new DefaultConfig().createRequestExecutor()).getStart(url,
				Lang.JSONLD);
	}

	@When("I create a Processor")
	public void iCreateAProcessor() {
		treeNodeProcessor = new TreeNodeProcessor(startingTreeNode, treeNodeRecordRepository, memberRepository,
				new TreeNodeFetcher(new DefaultConfig().createRequestExecutor()));
	}

	@Then("Member {string} is not processed")
	public void memberIsNotProcessed(String arg0) {
		assertFalse(memberRepository.isProcessed(new MemberRecord(arg0, null)));
	}

	@Then("Member {string} is processed")
	public void memberIsProcessed(String arg0) {
		assertTrue(memberRepository.isProcessed(new MemberRecord(arg0, null)));
	}

	@And("a StatePersistenceStrategy MEMORY")
	public void aMemoryStatePersistanceStrategy() {
		memberRepository = MemberRepositoryFactory.getMemberRepository(StatePersistenceStrategy.MEMORY);
		treeNodeRecordRepository = TreeNodeRecordRepositoryFactory
				.getTreeNodeRecordRepository(StatePersistenceStrategy.MEMORY);
	}

	@And("a StatePersistenceStrategy SQLITE")
	public void aSqliteStatePersistanceStrategy() {
		memberRepository = MemberRepositoryFactory.getMemberRepository(StatePersistenceStrategy.SQLITE);
		treeNodeRecordRepository = TreeNodeRecordRepositoryFactory
				.getTreeNodeRecordRepository(StatePersistenceStrategy.SQLITE);
	}
}
