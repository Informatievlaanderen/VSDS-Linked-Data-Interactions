package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.management.status.ClientStatusService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.After;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.treenodesupplier.domain.valueobject.ClientStatus;
import org.apache.jena.rdf.model.Model;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClient.NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties.URLS;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

public class LdioLdesClientITSteps extends LdesClientInIT {
	private final static WireMockServer wireMockServer = new WireMockServer(options().port(10101));
	private final String pipelineName = "pipelineName";
	private final ApplicationEventPublisher applicationEventPublisher = applicationEventPublisher();
	private final Map<String, String> componentPropsMap = new HashMap<>();
	private final List<Model> members = new ArrayList<>();
	private final ClientStatusService statusService = mock(ClientStatusService.class);
	private final String H2_URL = "jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH";
	private LdioInput ldioInput;

	@BeforeAll
	public static void before_all() {
		wireMockServer.start();
	}

	@After
	public void shutDown() {
		ldioInput.shutdown();
		;
	}

	@Given("I want to follow the following LDES")
	public void iWantToFollowTheFollowingLDES(List<String> urls) {
		AtomicInteger counter = new AtomicInteger();

		urls.forEach(url -> {
			String key = "%s.%d".formatted(URLS, counter.getAndIncrement());
			componentPropsMap.put(key, wireMockServer.baseUrl() + url);
		});
	}

	@And("I configure this to be of RDF format {string}")
	public void iConfigureThisToBeOfRDFFormat(String contentType) {
		componentPropsMap.put(LdioLdesClientProperties.SOURCE_FORMAT, contentType);
	}

	@When("^I start an ldes-ldio-in component")
	public void iStartAnLdesLdioInComponentWithUrl() {
		members.clear();
		ComponentExecutor componentExecutor = members::add;

		var props = new ComponentProperties(pipelineName, NAME, componentPropsMap);
		var ldioInputConfigurator = new LdioLdesClientAutoConfig()
				.ldioConfigurator(new StatePersistenceFactory(H2_URL, "sa", ""), statusService, null);
		ldioInput = ldioInputConfigurator.configure(null, componentExecutor, applicationEventPublisher, props);
	}

	@Then("All {int} members from the stream are passed to the pipeline")
	public void allMembersFromTheStreamArePassedToThePipeline(int memberCount) {
		await().atMost(Duration.ofMinutes(2)).until(() -> {
			System.out.println(members.size());
			return members.size() == memberCount;
		});
	}

	@And("I want to add the following properties")
	public void iWantToConfigureTheFollowingProperties(Map<String, String> properties) {
		this.componentPropsMap.putAll(properties);
	}

	@And("I expect {int} REPLICATING, {int} SYNCHRONISING and {int} COMPLETED updates")
	public void iExpectREPLICATINGSYNCHRONISINGAndCOMPLETEDUpdates(int replicatingTimes, int synchronisingTimes, int completedTimes) {
		verify(statusService, times(replicatingTimes)).updateStatus(pipelineName, ClientStatus.REPLICATING);
		verify(statusService, times(synchronisingTimes)).updateStatus(pipelineName, ClientStatus.SYNCHRONISING);
		verify(statusService, times(completedTimes)).updateStatus(pipelineName, ClientStatus.COMPLETED);
	}
}
