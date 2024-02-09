package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.Model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties.URLS;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.awaitility.Awaitility.await;

public class LdioLdesClientITSteps {
	private List<Model> members;
	static WireMockServer wireMockServer = new WireMockServer(options().port(10101));
	private final Map<String, String> componentPropsMap = new HashMap<>();

	@BeforeAll
	public static void before_all() {
		wireMockServer.start();
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
		members = new ArrayList<>();
		ComponentExecutor componentExecutor = linkedDataModel -> members.add(linkedDataModel);

		componentPropsMap.put(PIPELINE_NAME, "pipeline");

		var props = new ComponentProperties(componentPropsMap);
		var ldioInputConfigurator = new LdioLdesClientAutoConfig().ldioConfigurator(null);
		ldioInputConfigurator.configure(null, componentExecutor, props);
	}

	@Then("All {int} members from the stream are passed to the pipeline")
	public void allMembersFromTheStreamArePassedToThePipeline(int memberCount) {
		await().atMost(Duration.ofMinutes(2)).until(() -> {
			System.out.println(members.size());
			return members.size() == memberCount;
		});
	}

}
