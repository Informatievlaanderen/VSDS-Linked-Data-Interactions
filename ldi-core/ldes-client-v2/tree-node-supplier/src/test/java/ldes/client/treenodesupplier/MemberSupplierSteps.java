package ldes.client.treenodesupplier;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.requestexecutor.domain.valueobjects.DefaultConfig;
import ldes.client.treenodefetcher.TreeNodeFetcher;
import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;

import static org.junit.jupiter.api.Assertions.*;

public class MemberSupplierSteps {

	private Processor processor;
	private TreeNodeRecordRepository treeNodeRecordRepository;
	private MemberRepository memberRepository;
	private MemberSupplier memberSupplier;

	@Given("A Processor with a TreeNodeRepository, a MemberRepository and a starting url {string}")
	public void aProcessorWithATreeNodeRepositoryAMemberRepositoryAndAStartingUrl(String arg0) {
		treeNodeRecordRepository = new InMemoryTreeNodeRecordRepository();
		memberRepository = new InMemoryMemberRepository();
		processor = new Processor(new TreeNodeRecord(arg0), treeNodeRecordRepository, memberRepository,
				new TreeNodeFetcher(new DefaultConfig().createRequestExecutor()));
	}

	@When("I request the {int} members from the MemberSupplier")
	public void iRequestTheMembersFromTheMemberSupplier(int arg0) {
		for (int i = 0; i < arg0; i++) {
			memberSupplier.get();
		}
	}

	@And("I create a MemberSupplier")
	public void iCreateAMemberSupplier() {
		memberSupplier = new MemberSupplier(processor);
	}

	@Then("The TreeNode is processed: {string}")
	public void theTreeNodeIsProcessed(String arg0) {
		assertTrue(treeNodeRecordRepository.existsById(arg0));
	}

	@Then("The TreeNode is not processed: {string}")
	public void theTreeNodeIsNotProcessed(String arg0) {
		assertFalse(treeNodeRecordRepository.existsById(arg0));
	}

	@Then("Status {string} for TreeNodeRecord with identifier: {string}")
	public void statusForTreeNodeRecordWithIdentifier(String arg0, String arg1) {
		assertTrue(treeNodeRecordRepository.existsByIdAndStatus(arg1, TreeNodeStatus.valueOf(arg0)));
	}
}
