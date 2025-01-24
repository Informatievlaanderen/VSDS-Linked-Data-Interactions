package ldes.client.treenodesupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateUtil;
import be.vlaanderen.informatievlaanderen.ldes.ldi.postgres.PostgresProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite.SqliteProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampFromCurrentTimeExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampFromPathExtractor;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.treenodesupplier.domain.valueobject.*;
import ldes.client.treenodesupplier.filters.ExactlyOnceFilter;
import ldes.client.treenodesupplier.filters.LatestStateFilter;
import ldes.client.treenodesupplier.filters.MemberFilter;
import ldes.client.treenodesupplier.membersuppliers.FilteredMemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplierImpl;
import org.apache.jena.riot.Lang;
import org.junit.After;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class MemberSupplierSteps {
	private final RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory(false);
	private TreeNodeProcessor treeNodeProcessor;
	private LdesClientRepositories ldesClientRepositories;
	private MemberSupplier memberSupplier;
	private LdesMetaData ldesMetaData;
	private SuppliedMember suppliedMember;
	private PostgreSQLContainer<?> postgreSQLContainer;
	private final Consumer<ClientStatus> clientStatusConsumer = Mockito.mock(Consumer.class);

	// Multi MemberSupplier
	private final MemberSupplier[] memberSuppliers = new MemberSupplier[2];
	private final SuppliedMember[] suppliedMembers = new SuppliedMember[2];

	private String timestampPath;

	@Before
	public void setup() {
		timestampPath = "";
	}

	@After
	public void teardown() {
		if (postgreSQLContainer != null) {
			postgreSQLContainer.stop();
			postgreSQLContainer = null;
		}
	}

	@When("I request one member from the MemberSupplier")
	public void iRequestOneMemberFromTheMemberSupplier() {
		suppliedMember = memberSupplier.get();
	}

	@And("The TreeNode is not processed: {string}")
	public void theTreeNodeIsNotProcessed(String treeNodeId) {
		assertFalse(ldesClientRepositories.treeNodeRecordRepository().existsById(treeNodeId));
	}

	@Then("Status {string} for TreeNodeRecord with identifier: {string}")
	public void statusForTreeNodeRecordWithIdentifier(String treeNodeStatus, String treeNodeId) {
		assertTrue(ldesClientRepositories.treeNodeRecordRepository().existsByIdAndStatus(treeNodeId, TreeNodeStatus.valueOf(treeNodeStatus)));
	}

	@Given("A starting url {string}")
	public void aStartingUrl(String url) {
		ldesMetaData = new LdesMetaData(List.of(url), Lang.JSONLD);
	}

	@Given("^Starting urls$")
	public void startingUrls(List<String> urls) {
		ldesMetaData = new LdesMetaData(urls, Lang.TURTLE);
	}

	@Given("I set a timestamp path {string}")
	public void setTimestampPath(String timestampPath) {
		this.timestampPath = timestampPath;
	}

	@When("I create a Processor")
	public void iCreateAProcessor() {
		treeNodeProcessor = new TreeNodeProcessor(ldesMetaData,
				ldesClientRepositories,
				requestExecutorFactory.createNoAuthExecutor(),
				timestampPath.isEmpty() ? new TimestampFromCurrentTimeExtractor() : new TimestampFromPathExtractor(createProperty(timestampPath)),
				clientStatusConsumer);
	}

	@Then("Member {string} is processed")
	public void memberIsProcessed(String memberId) {
		assertThat(suppliedMember.getId()).isEqualTo(memberId);
	}

	@And("a StatePersistenceStrategy MEMORY")
	public LdesClientRepositories aMemoryStatePersistenceStrategy() {
		LdesClientRepositories clientRepositories = LdesClientRepositories.memoryBased();

		ldesClientRepositories = new LdesClientRepositories(clientRepositories.memberRepository(), clientRepositories.memberIdRepository(),
				clientRepositories.treeNodeRecordRepository(), clientRepositories.memberVersionRepository());
		return ldesClientRepositories;
	}

	@And("a StatePersistenceStrategy SQLITE")
	public LdesClientRepositories aSqliteStatePersistenceStrategy() {
		final SqliteProperties sqliteProperties = new SqliteProperties("target", UUID.randomUUID().toString(), false);
		LdesClientRepositories clientRepositories = LdesClientRepositories.sqlBased(
				HibernateUtil.createEntityManagerFromProperties(sqliteProperties.getProperties()));
		ldesClientRepositories = new LdesClientRepositories(clientRepositories.memberRepository(), clientRepositories.memberIdRepository(),
				clientRepositories.treeNodeRecordRepository(), clientRepositories.memberVersionRepository());
		return ldesClientRepositories;
	}

	@And("a StatePersistenceStrategy POSTGRES")
	public LdesClientRepositories aPostgresStatePersistenceStrategy() {
		postgreSQLContainer = new PostgreSQLContainer<>("postgres:11.1")
				.withDatabaseName("integration-test-client-persistence")
				.withUsername("sa")
				.withPassword("sa");
		postgreSQLContainer.start();

		PostgresProperties postgresProperties = new PostgresProperties(postgreSQLContainer.getJdbcUrl(),
				postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword(), false);
		LdesClientRepositories clientRepositories = LdesClientRepositories.sqlBased(
				HibernateUtil.createEntityManagerFromProperties(postgresProperties.getProperties()));

		ldesClientRepositories = new LdesClientRepositories(clientRepositories.memberRepository(), clientRepositories.memberIdRepository(),
				clientRepositories.treeNodeRecordRepository(), clientRepositories.memberVersionRepository());
		return ldesClientRepositories;
	}

	@Then("MemberSupplier is destroyed")
	public void membersupplierIsDestroyed() {
		memberSupplier.destroyState();
	}


	@When("I create a MemberSupplier with state")
	public void iCreateAMemberSupplierWithState() {
		memberSupplier = new MemberSupplierImpl(treeNodeProcessor, true);
		memberSupplier.init();
	}

	@When("I create a MemberSupplier without state")
	public void iCreateAMemberSupplierWithoutState() {
		memberSupplier = new MemberSupplierImpl(treeNodeProcessor, false);
		memberSupplier.init();
	}

	@When("I create a MemberSupplier with ExactlyOnceFilter")
	public void iCreateAMemberSupplierWithFilter() {
		final boolean keepState = false;
		memberSupplier = new FilteredMemberSupplier(new MemberSupplierImpl(treeNodeProcessor, keepState),
				new ExactlyOnceFilter(ldesClientRepositories.memberIdRepository(), keepState));
		memberSupplier.init();
	}

	private LdesClientRepositories defineStatePersistence(String persistenceStrategy) {
		return switch (persistenceStrategy) {
			case "POSTGRES" -> aPostgresStatePersistenceStrategy();
			case "SQLITE" -> aSqliteStatePersistenceStrategy();
			case "MEMORY" -> aMemoryStatePersistenceStrategy();
			default -> null;
		};
	}

	@And("a {word} MemberSupplier and a {word} MemberSupplier")
	public void aStatePersistenceStrategyProcessorAndAStatePersistenceStrategyProcessor(String arg0, String arg1) {
		timestampPath = "http://www.w3.org/ns/prov#generatedAtTime";

		memberSuppliers[0] = new MemberSupplierImpl(new TreeNodeProcessor(ldesMetaData,
				defineStatePersistence(arg0),
				requestExecutorFactory.createNoAuthExecutor(),
				new TimestampFromPathExtractor(createProperty(timestampPath)), clientStatusConsumer), false);
		memberSuppliers[1] = new MemberSupplierImpl(new TreeNodeProcessor(ldesMetaData,
				defineStatePersistence(arg1),
				requestExecutorFactory.createNoAuthExecutor(),
				new TimestampFromPathExtractor(createProperty(timestampPath)), clientStatusConsumer), false);
	}

	@When("I request one member from the MemberSuppliers")
	public void iRequestOneMemberFromTheMemberSuppliers() {
		for(MemberSupplier supplier : memberSuppliers) {
			supplier.init();
		}
		suppliedMembers[0] = memberSuppliers[0].get();
		suppliedMembers[1] = memberSuppliers[1].get();
	}

	@Then("Member {string} is processed in both MemberSuppliers")
	public void memberIsProcessedInBothMemberSuppliers(String memberId) {
		var property = createProperty("http://purl.org/dc/terms/created");

		var supplier0MemberId = suppliedMembers[0].getModel().listSubjectsWithProperty(property).nextResource().asNode().toString();
		assertEquals(memberId, supplier0MemberId);
		var supplier1MemberId = suppliedMembers[1].getModel().listSubjectsWithProperty(property).nextResource().asNode().toString();
		assertEquals(memberId, supplier1MemberId);
	}

	@Then("MemberSuppliers are destroyed")
	public void membersuppliersAreDestroyed() {
		memberSuppliers[0].destroyState();
		memberSuppliers[1].destroyState();
	}

	@When("I create a MemberSupplier with LatestStateFilter")
	public void iCreateAMemberSupplierWithLatestStateFilter() {
		final boolean keepState = false;
		final MemberSupplierImpl baseMemberSupplier = new MemberSupplierImpl(treeNodeProcessor, keepState);
		final MemberFilter latestStateFilter = new LatestStateFilter(ldesClientRepositories.memberVersionRepository(), keepState, timestampPath, "http://purl.org/dc/terms/isVersionOf");
		memberSupplier = new FilteredMemberSupplier(baseMemberSupplier, latestStateFilter);
		memberSupplier.init();
	}
}
