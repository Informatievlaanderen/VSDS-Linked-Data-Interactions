package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpSparqlOutAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpSparqlOutProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.ConfigPropertyMissingException;
import com.github.tomakehurst.wiremock.client.BasicCredentials;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WireMockTest(httpPort = LdioHttpSparqlOutTest.PORT)
class LdioHttpSparqlOutTest {
	static final int PORT = 12121;
	private static final String PIPELINE_NAME = "test-pipeline";
	private static final String ENDPOINT = "http://localhost:%s/sparql".formatted(PORT);
	private static final Map<String, String> minimalProperties = Map.of(
			LdioHttpSparqlOutProperties.ENDPOINT, ENDPOINT,
			"graph", "http://example.graph.com"
	);
	private static Model model;
	private final LdioHttpSparqlOutAutoConfig.LdioHttpSparqlOutConfigurator configurator = new LdioHttpSparqlOutAutoConfig().ldiHttpSparqlOutConfigurator();
	private LdiOutput ldioHttpSparqlOut;

	@BeforeAll
	static void beforeAll() {
		model = RDFParser.source("mob-hind-model.ttl").lang(Lang.TTL).toModel();
	}

	@BeforeEach
	void setUp() {
		stubFor(post(urlEqualTo("/sparql")).willReturn(ok()));
	}

	@Test
	void given_ValidModel_when_SparqlOut_then_PostQuery() {
		final Map<String, String> properties = Map.of(
				LdioHttpSparqlOutProperties.ENDPOINT, ENDPOINT,
				LdioHttpSparqlOutProperties.REPLACEMENT_ENABLED, Boolean.FALSE.toString()
		);
		final String content = "<http://localhost:8080/people> <http://schema.org/name> \"Jane Doe\" .\n";
		final Model contentModel = RDFParser.create().fromString(content).lang(Lang.NQUADS).toModel();
		final String expectedRequestBody = "INSERT DATA { %s }".formatted(content);
		ldioHttpSparqlOut = configurator.configure(new ComponentProperties(PIPELINE_NAME, LdioHttpSparqlOut.NAME, properties));

		ldioHttpSparqlOut.accept(contentModel);

		verify(postRequestedFor(urlEqualTo("/sparql")).withRequestBody(containing(expectedRequestBody)));
	}

	@Test
	void given_SkolemisationEnabled_when_SparqlOut_then_PostQuery() {
		final String skolemDomain = "http://example.com";
		final Map<String, String> properties = extendProperties(Map.of("skolemisation.skolemDomain", skolemDomain));
		ldioHttpSparqlOut = configurator.configure(new ComponentProperties(PIPELINE_NAME, LdioHttpSparqlOut.NAME, properties));

		ldioHttpSparqlOut.accept(model);

		verify(postRequestedFor(urlEqualTo("/sparql")).withRequestBody(containing("%s/.well-known/genid/".formatted(skolemDomain))));
	}

	@Test
	void given_GraphOmitted_when_SparqlOut_then_PostQuery() {
		final Map<String, String> properties = Map.of(LdioHttpSparqlOutProperties.ENDPOINT, ENDPOINT);
		ldioHttpSparqlOut = configurator.configure(new ComponentProperties(PIPELINE_NAME, LdioHttpSparqlOut.NAME, properties));

		ldioHttpSparqlOut.accept(model);

		verify(postRequestedFor(urlEqualTo("/sparql")).withRequestBody(notContaining("FROM")));
	}

	@Test
	void given_CustomDeteFunction_when_SparqlOut_then_PostQuery() {
		final String customDeleteFunction = "custom function";
		final Map<String, String> properties = extendProperties(Map.of(LdioHttpSparqlOutProperties.REPLACEMENT_DELETE_FUNCTION, customDeleteFunction));
		ldioHttpSparqlOut = configurator.configure(new ComponentProperties(PIPELINE_NAME, LdioHttpSparqlOut.NAME, properties));

		ldioHttpSparqlOut.accept(model);

		verify(postRequestedFor(urlEqualTo("/sparql")).withRequestBody(containing(customDeleteFunction).and(notContaining("FROM"))));
	}

	@ParameterizedTest
	@ArgumentsSource(AuthPropertiesProvider.class)
	void given_AuthConfig_when_SparqlOut_then_PostQueryWithHeader(Map<String, String> authProperties) {
		final Map<String, String> properties = extendProperties(authProperties);
		ldioHttpSparqlOut = configurator.configure(new ComponentProperties(PIPELINE_NAME, LdioHttpSparqlOut.NAME, properties));

		ldioHttpSparqlOut.accept(model);

		verify(postRequestedFor(urlEqualTo("/sparql")).withBasicAuth(AuthPropertiesProvider.basicCredentials));
	}

	@Test
	void when_EndpointIsNotConfigured_then_ThrowMissingConfigPropertyException() {
		ComponentProperties componentProperties = new ComponentProperties(PIPELINE_NAME, LdioHttpSparqlOut.NAME);
		assertThatThrownBy(() -> configurator.configure(componentProperties))
				.isExactlyInstanceOf(ConfigPropertyMissingException.class)
				.hasMessage("Pipeline \"test-pipeline\": \"Ldio:HttpSparqlOut\" : Missing value for property \"endpoint\" .");
	}

	private Map<String, String> extendProperties(Map<String, String> properties) {
		final Map<String, String> extendedProperties = new HashMap<>(minimalProperties);
		extendedProperties.putAll(properties);
		return extendedProperties;
	}

	private static class AuthPropertiesProvider implements ArgumentsProvider {
		static final String USERNAME = "sparql";
		static final String PASSWORD = "changeme";
		static final BasicCredentials basicCredentials = new BasicCredentials(USERNAME, PASSWORD);

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
			return Stream.of(
					Map.of(
							"auth.type", "API_KEY",
							"auth.api-key", basicCredentials.asAuthorizationHeaderValue(),
							"auth.api-key-header", "Authorization"
					),
					Map.of(
							"http.headers.0.key", "Authorization",
							"http.headers.0.value", basicCredentials.asAuthorizationHeaderValue()
							)
			).map(Arguments::of);
		}
	}
}