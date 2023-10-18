package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.EmptyPropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.riot.RDFLanguages.nameToLang;
import static org.junit.jupiter.api.Assertions.*;

public class HttpEnricherSteps {

	private List<Model> resultModels;
	private Model inputModel;
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

	@And("I configure body property path {string}")
	public void iConfigureBodyPropertyPath(String bodyPropertyPath) {
		this.bodyPropertyPath = bodyPropertyPath;
	}

	@And("I configure header property path {string}")
	public void iConfigureHeaderPropertyPath(String headerPropertyPath) {
		this.headerPropertyPath = headerPropertyPath;
	}

	@And("I configure httpMethod property path {string}")
	public void iConfigureHttpMethodPropertyPath(String httpMethodPropertyPath) {
		this.httpMethodPropertyPath = httpMethodPropertyPath;
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
		inputModel = RDFParser.source("model-with-only-url.ttl").toModel();
	}

	@And("I have a model with everything")
	public void iHaveAModelWithEverything() {
		inputModel = RDFParser.source("model-with-everything.ttl").toModel();
	}

	@And("I have a model with an invalid http method")
	public void iHaveAModelWithAnInvalidHttpMethod() {
		inputModel = RDFParser.source("model-with-invalid-http-method.ttl").toModel();
	}

	@When("I send the model to the enricher")
	public void iSendTheModelToTheEnricher() {
		resultModels = new ArrayList<>(ldioHttpEnricher.apply(inputModel));
	}

	@Then("The result contains {int} model")
	public void theResultContainsModel(int resultCount) {
		assertEquals(resultCount, resultModels.size());
	}

	@Then("The result contains a model with both the input and the http response")
	public void theResultContainsAModelWithBothTheInputAndTheHttpResponse() {
		Model resultModel = this.resultModels.get(0);

		final Model expectedModel = ModelFactory.createDefaultModel();
		expectedModel.add(inputModel);
		expectedModel.add(
				createResource("http://example.org/John"),
				createProperty("http://example.org/hasWife"),
				createResource("http://example.org/Hillary"));

		assertTrue(expectedModel.isIsomorphicWith(resultModel));
	}

	@Then("The enricher should throw an exception when I send the model to the enricher")
	public void theEnricherShouldThrowAnExceptionWhenISendTheModelToTheEnricher() {
		assertThrows(IllegalStateException.class, () -> ldioHttpEnricher.apply(inputModel));
	}
}
