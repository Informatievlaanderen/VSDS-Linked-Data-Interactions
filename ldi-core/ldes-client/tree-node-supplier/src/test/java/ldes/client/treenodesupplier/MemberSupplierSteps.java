package ldes.client.treenodesupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth.DefaultConfig;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.treenodesupplier.domain.services.MemberRepositoryFactory;
import ldes.client.treenodesupplier.domain.services.TreeNodeRecordRepositoryFactory;
import ldes.client.treenodesupplier.domain.valueobject.*;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.sql.postgres.PostgresProperties;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemberSupplierSteps {
	private TreeNodeProcessor treeNodeProcessor;
	private TreeNodeRecordRepository treeNodeRecordRepository;
	private MemberRepository memberRepository;
	private MemberSupplier memberSupplier;
	private LdesMetaData ldesMetaData;
	private SuppliedMember suppliedMember;
	private PostgreSQLContainer postgreSQLContainer;

	// Multi MemberSupplier
	private final MemberSupplier[] memberSuppliers = new MemberSupplier[2];
	private SuppliedMember[] suppliedMembers = new SuppliedMember[2];

	@When("I request one member from the MemberSupplier")
	public void iRequestOneMemberFromTheMemberSupplier() {
		suppliedMember = memberSupplier.get();
	}

	@And("The TreeNode is not processed: {string}")
	public void theTreeNodeIsNotProcessed(String treeNodeId) {
		assertFalse(treeNodeRecordRepository.existsById(treeNodeId));
	}

	@Then("Status {string} for TreeNodeRecord with identifier: {string}")
	public void statusForTreeNodeRecordWithIdentifier(String treeNodeStatus, String treeNodeId) {
		assertTrue(treeNodeRecordRepository.existsByIdAndStatus(treeNodeId, TreeNodeStatus.valueOf(treeNodeStatus)));
	}

	@Given("A starting url {string}")
	public void aStartingUrl(String url) {
		ldesMetaData = new LdesMetaData(url,
				Lang.JSONLD);
	}

	@When("I create a Processor")
	public void iCreateAProcessor() {
		treeNodeProcessor = new TreeNodeProcessor(ldesMetaData,
				new StatePersistence(memberRepository, treeNodeRecordRepository),
				new DefaultConfig().createRequestExecutor(new ArrayList<>()));
	}

	@Then("Member {string} is processed")
	public void memberIsProcessed(String memberId) {
		assertTrue(toString(suppliedMember.getModel(), Lang.JSONLD).contains(memberId));
	}

	@And("a StatePersistenceStrategy MEMORY")
	public StatePersistence aMemoryStatePersistenceStrategy() {
		memberRepository = MemberRepositoryFactory.getMemberRepository(StatePersistenceStrategy.MEMORY, Map.of(),
				"instanceName");
		treeNodeRecordRepository = TreeNodeRecordRepositoryFactory
				.getTreeNodeRecordRepository(StatePersistenceStrategy.MEMORY, Map.of(), "instanceName");
		return new StatePersistence(memberRepository, treeNodeRecordRepository);
	}

	@And("a StatePersistenceStrategy SQLITE")
	public StatePersistence aSqliteStatePersistenceStrategy() {
		memberRepository = MemberRepositoryFactory.getMemberRepository(StatePersistenceStrategy.SQLITE, Map.of(),
				"instanceName");
		treeNodeRecordRepository = TreeNodeRecordRepositoryFactory
				.getTreeNodeRecordRepository(StatePersistenceStrategy.SQLITE, Map.of(), "instanceName");
		return new StatePersistence(memberRepository, treeNodeRecordRepository);
	}

	@And("a StatePersistenceStrategy POSTGRES")
	public StatePersistence aPostgresStatePersistenceStrategy() {
		PostgresProperties postgresProperties = new PostgresProperties(postgreSQLContainer.getJdbcUrl(),
				postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword(), false);
		memberRepository = MemberRepositoryFactory.getMemberRepository(StatePersistenceStrategy.POSTGRES,
				postgresProperties.getProperties(), "instanceName");
		treeNodeRecordRepository = TreeNodeRecordRepositoryFactory
				.getTreeNodeRecordRepository(StatePersistenceStrategy.POSTGRES, postgresProperties.getProperties(),
						"instanceName");

		return new StatePersistence(memberRepository, treeNodeRecordRepository);
	}

	@And("a StatePersistenceStrategy FILE")
	public StatePersistence aFileStatePersistenceStrategy() {
		memberRepository = MemberRepositoryFactory.getMemberRepository(StatePersistenceStrategy.FILE, Map.of(),
				"instanceName");
		treeNodeRecordRepository = TreeNodeRecordRepositoryFactory
				.getTreeNodeRecordRepository(StatePersistenceStrategy.FILE, Map.of(), "instanceName");

		return new StatePersistence(memberRepository, treeNodeRecordRepository);
	}

	@Then("MemberSupplier is destroyed")
	public void membersupplierIsDestroyed() {
		memberSupplier.destroyState();
	}

	private String toString(final Model model, final Lang lang) {
		StringWriter stringWriter = new StringWriter();
		RDFDataMgr.write(stringWriter, model, lang);
		return stringWriter.toString();
	}

	@When("I create a MemberSupplier with state")
	public void iCreateAMemberSupplierWithState() {
		memberSupplier = new MemberSupplier(treeNodeProcessor, true);
	}

	@When("I create a MemberSupplier without state")
	public void iCreateAMemberSupplierWithoutState() {
		memberSupplier = new MemberSupplier(treeNodeProcessor, false);
	}

	@And("Postgres TestContainer is started")
	public void postgresTestcontainerIsStarted() {
		postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
				.withDatabaseName("integration-test-client-persistence")
				.withUsername("sa")
				.withPassword("sa");
		postgreSQLContainer.start();
	}

	@And("Postgres TestContainer is stopped")
	public void postgresTestContainerIsStopped() {
		if (postgreSQLContainer != null) {
			postgreSQLContainer.stop();
			postgreSQLContainer = null;
		}
	}

	private StatePersistence defineStatePersistence(String persistenceStrategy) {
		return switch (persistenceStrategy) {
			case "FILE" -> aFileStatePersistenceStrategy();
			case "POSTGRES" -> aPostgresStatePersistenceStrategy();
			case "SQLITE" -> aSqliteStatePersistenceStrategy();
			case "MEMORY" -> aMemoryStatePersistenceStrategy();
			default -> null;
		};
	}

	@And("a {word} MemberSupplier and a {word} MemberSupplier")
	public void aStatePersistenceStrategyProcessorAndAStatePersistenceStrategyProcessor(String arg0, String arg1) {
		memberSuppliers[0] = new MemberSupplier(new TreeNodeProcessor(ldesMetaData,
				defineStatePersistence(arg0),
				new DefaultConfig().createRequestExecutor(new ArrayList<>())), false);
		memberSuppliers[1] = new MemberSupplier(new TreeNodeProcessor(ldesMetaData,
				defineStatePersistence(arg0),
				new DefaultConfig().createRequestExecutor(new ArrayList<>())), false);
	}

	@When("I request one member from the MemberSuppliers")
	public void iRequestOneMemberFromTheMemberSuppliers() {
		suppliedMembers[0] = memberSuppliers[0].get();
		suppliedMembers[1] = memberSuppliers[1].get();
	}

	@Then("Member {string} is processed in both MemberSuppliers")
	public void memberIsProcessedInBothMemberSuppliers(String memberId) {
		assertTrue(toString(suppliedMembers[0].getModel(), Lang.JSONLD).contains(memberId));
		assertTrue(toString(suppliedMembers[1].getModel(), Lang.JSONLD).contains(memberId));
	}

	@Then("MemberSuppliers are destroyed")
	public void membersuppliersAreDestroyed() {
		memberSuppliers[0].destroyState();
		memberSuppliers[1].destroyState();
	}
}
