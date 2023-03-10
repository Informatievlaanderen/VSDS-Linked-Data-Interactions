package ldes.client.treenodesupplier;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.requestexecutor.domain.valueobjects.DefaultConfig;
import ldes.client.treenodefetcher.TreeNodeFetcher;
import ldes.client.treenodesupplier.domain.valueobject.Ldes;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryTreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.sqlite.SqliteMemberRepository;
import ldes.client.treenodesupplier.repository.sqlite.SqliteTreeNodeRepository;
import org.apache.jena.riot.Lang;

import static org.junit.jupiter.api.Assertions.*;

public class MemberSupplierSteps {

	private TreeNodeProcessor treeNodeProcessor;
	private TreeNodeRecordRepository treeNodeRecordRepository;
	private MemberRepository memberRepository;
	private MemberSupplier memberSupplier;
	private Ldes ldes;

	@Given("A Processor with a TreeNodeRepository, a MemberRepository and a starting url {string}")
	public void aProcessorWithATreeNodeRepositoryAMemberRepositoryAndAStartingUrl(String arg0) {
		treeNodeRecordRepository = new InMemoryTreeNodeRecordRepository();
		memberRepository = new InMemoryMemberRepository();
		treeNodeProcessor = new TreeNodeProcessor(new Ldes(arg0, Lang.JSONLD), treeNodeRecordRepository,
				memberRepository,
				new TreeNodeFetcher(new DefaultConfig().createRequestExecutor()), false);
	}

	@When("I request the {int} members from the MemberSupplier")
	public void iRequestTheMembersFromTheMemberSupplier(int arg0) {
		for (int i = 0; i < arg0; i++) {
			memberSupplier.get();
		}
	}

	@When("I create a MemberSupplier")
	public void iCreateAMemberSupplier() {
		memberSupplier = new MemberSupplier(treeNodeProcessor);
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

	@Given("A Processor with a sqlite TreeNodeRepository, a sqlite MemberRepository and a starting url {string}")
	public void aProcessorWithATreeNodeRepositoryASqliteMemberRepositoryAndAStartingUrl(String arg0) {
		treeNodeRecordRepository = new SqliteTreeNodeRepository();
		memberRepository = new SqliteMemberRepository();
		treeNodeProcessor = new TreeNodeProcessor(new Ldes(arg0, Lang.JSONLD), treeNodeRecordRepository,
				memberRepository,
				new TreeNodeFetcher(new DefaultConfig().createRequestExecutor()), false);
	}

	@Given("A starting url {string}")
	public void aStartingUrl(String arg0) {
		ldes = new Ldes(arg0, Lang.JSONLD);
	}

	@And("a InMemoryMemberRepository and a InMemoryTreeNodeRecordRepository")
	public void inMemoryRepositories() {
		memberRepository = new InMemoryMemberRepository();
		treeNodeRecordRepository = new InMemoryTreeNodeRecordRepository();

	}

	@And("a SqliteMemberRepository and a SqliteTreeNodeRepository")
	public void sqliteRepositories() {
		memberRepository = new SqliteMemberRepository();
		treeNodeRecordRepository = new SqliteTreeNodeRepository();

	}

	@When("I create a Processor")
	public void iCreateAProcessor() {
		treeNodeProcessor = new TreeNodeProcessor(ldes, treeNodeRecordRepository, memberRepository,
				new TreeNodeFetcher(new DefaultConfig().createRequestExecutor()), false);
	}
}
