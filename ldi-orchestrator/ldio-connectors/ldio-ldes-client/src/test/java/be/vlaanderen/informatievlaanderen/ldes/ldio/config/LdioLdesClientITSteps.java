package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.jena.rdf.model.Model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.awaitility.Awaitility.await;

public class LdioLdesClientITSteps {
	private List<Model> members;
	static WireMockServer wireMockServer = new WireMockServer(options().port(10101));

	@BeforeAll
	public static void before_all() {
		wireMockServer.start();
	}

	@Given("I start an ldes-ldio-in component with url {string}")
	public void iStartAnLdesLdioInComponentWithUrl(String url) {
		members = new ArrayList<>();
		ComponentExecutor componentExecutor = linkedDataModel -> members.add(linkedDataModel);
		var props = new ComponentProperties(Map.of(PIPELINE_NAME, "pipeline",
				LdioLdesClientProperties.URL, wireMockServer.baseUrl() + url));
		var ldioInputConfigurator = new LdioLdesClientAutoConfig().ldioConfigurator();
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
