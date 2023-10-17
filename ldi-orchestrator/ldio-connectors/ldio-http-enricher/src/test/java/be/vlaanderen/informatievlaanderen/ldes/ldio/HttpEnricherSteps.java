package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.EmptyPropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor.LdioRequestExecutorSupplier;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;

import java.util.Collection;
import java.util.stream.Stream;

import static org.apache.jena.riot.RDFLanguages.nameToLang;

public class HttpEnricherSteps {

	private Collection<Model> result;
	private Model model;
	private LdioHttpEnricher ldioHttpEnricher;
	private LdiAdapter ldiAdapter;
	private String urlPropertyPath;
	private String bodyPropertyPath;
	private String headerPropertyPath;
	private String httpMethodPropertyPath;

	@Given("I have an RdfAdapter")
	public void iHaveAnRdfAdapter() {
		ldiAdapter = input -> Stream.of(
				RDFParserBuilder
						.create()
						.fromString(input.content())
						.lang(nameToLang(input.mimeType()))
						.toModel());
	}

	@And("I configure url property path {string}")
	public void iConfigureUrlPropertyPath(String urlPropertyPath) {
		this.urlPropertyPath = urlPropertyPath;
	}

	@And("I create an LdioHttpEnricher with the configured properties")
	public void iCreateAnLdioHttpEnricherWithTheConfiguredProperties() {
		final RequestExecutor requestExecutor = new RequestExecutorFactory().createNoAuthExecutor();
		var propertyPathExtractors = new RequestPropertyPathExtractors(
				getPropertyPathExtractor(urlPropertyPath),
				getPropertyPathExtractor(bodyPropertyPath),
				getPropertyPathExtractor(headerPropertyPath),
				getPropertyPathExtractor(httpMethodPropertyPath));
		ldioHttpEnricher = new LdioHttpEnricher(ldiAdapter, requestExecutor, propertyPathExtractors);
	}

	private PropertyExtractor getPropertyPathExtractor(String propertyPath) {
		return propertyPath != null ? PropertyPathExtractor.from(propertyPath) : new EmptyPropertyExtractor();
	}

	@And("I have a model with only an url property")
	public void iHaveAModelWithOnlyAnUrlProperty() {
		model = RDFParser.source("model-with-only-url.ttl").toModel();
	}

	@When("I send the model to the enricher")
	public void iSendTheModelToTheEnricher() {
		result = ldioHttpEnricher.apply(model);
	}

	@Then("The result contains a model with both the input and the http response")
	public void theResultContainsAModelWithBothTheInputAndTheHttpResponse() {
		// TODO TVB: 17/10/23 impl isomorph test
		System.out.println(result.stream().findFirst().orElseThrow().listStatements().toList());
	}
}
