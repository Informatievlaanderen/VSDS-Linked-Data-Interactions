package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioJsonToLdAdapterAutoConfig.NAME;
import static org.junit.Assert.assertTrue;

public class JsonToLdIntegrationSteps {

	private Map<String, String> config;
	private LdiComponent adapter;
	private Model output;

	@Given("I set a core context in the configuration")
	public void setConfig() {
		config = new HashMap<>();
		config.put("core-context", "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld");
	}

	@And("I create the adapter")
	public void iCreateAdapter() {
		ComponentProperties properties = new ComponentProperties("pipeline", NAME, config);
		adapter = new LdioJsonToLdAdapterAutoConfig.LdioJsonToLdConfigurator().configure(properties);
	}

	@When("I send a json object")
	public void sendJson() throws IOException {
		LdiAdapter.Content content = new LdiAdapter.Content(
				Files.readString(Path.of("src/test/resources/example.json")), "application/json");
		output = ((LdiAdapter) adapter).apply(content).toList().get(0);
	}

	@Then("The context is added")
	public void compareOutput() throws IOException {
		assertTrue(readModelFromFile("src/test/resources/expected.json").isIsomorphicWith(output));
	}

	private Model readModelFromFile(String path) throws IOException {
		String data = Files.readString(Path.of(path));
		Model model = ModelFactory.createDefaultModel();
		RDFParser.fromString(data)
				.lang(Lang.JSONLD)
				.parse(model);
		return model;
	}
}
