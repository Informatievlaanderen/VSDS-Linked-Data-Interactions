package be.vlaanderen.informatievlaanderen.ldes.client.services;

import be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults;
import be.vlaanderen.informatievlaanderen.ldes.client.LdesClientImplFactory;
import be.vlaanderen.informatievlaanderen.ldes.client.config.LdesClientConfig;
import be.vlaanderen.informatievlaanderen.ldes.client.exceptions.FragmentFetcherException;
import be.vlaanderen.informatievlaanderen.ldes.client.exceptions.UnparseableFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesFragment;
import org.apache.http.impl.client.HttpClients;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.time.LocalDateTime;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@WireMockTest(httpPort = LdesFragmentFetcherImplTest.HTTP_PORT)
class LdesFragmentFetcherImplTest {

	public static final int HTTP_PORT = 10101;

	private final String initialFragmentUrl = "http://localhost:" + HTTP_PORT + "/exampleData";
	private final String actualFragmentUrl = "http://localhost:" + HTTP_PORT
			+ "/exampleData?generatedAtTime=2022-05-05T00:00:00.000Z";
	private final String invalidFragmentUrl = "http://localhost:" + HTTP_PORT + "/invalid_format";
	private final String expirationDateSetFragmentUrl = "http://localhost:" + HTTP_PORT + "/expiration-date-set";
	private final String expirationDateNotSetFragmentUrl = "http://localhost:" + HTTP_PORT + "/expiration-date-not-set";
	private final String http304ResponseFragmentUrl = "http://localhost:" + HTTP_PORT + "/304-response";
	private final String apiKeyFragmentUrl = "http://localhost:" + HTTP_PORT + "/api-key";

	private LdesClientConfig config = new LdesClientConfig();
	private LdesService ldesService = LdesClientImplFactory.getLdesService(config);
	private LdesFragmentFetcher fragmentFetcher = LdesClientImplFactory.getFragmentFetcher(config);

	private String configWithApiKey = "application-api-key.properties";
	private String configApiKey = "b739fcae-a312-4fb4-a9eb-1d85da926e57";
	private String apiKey = "9e3e9b5e-d2da-4daa-ba6c-baca62f49f7e";

	@AfterAll
	void tearDown() {
		ldesService.getStateManager().destroyState();
	}

	@Test
	void whenInitialised_thenFragmentFetcherHasConfig() {
		LdesClientConfig config;
		LdesClientConfig loadedConfig;
		LdesFragmentFetcherImpl configuredFragmentFetcher;

		config = new LdesClientConfig();
		configuredFragmentFetcher = new LdesFragmentFetcherImpl(config, HttpClients.createDefault());
		loadedConfig = configuredFragmentFetcher.config;

		assertEquals(LdesClientDefaults.DEFAULT_API_KEY_HEADER, loadedConfig.getApiKeyHeader());
		assertNull(loadedConfig.getApiKey());

		// Configured config
		config = new LdesClientConfig();
		String header = "TEST_API_HEADER";
		String key = "TEST_API_KEY";
		config.setApiKeyHeader(header);
		config.setApiKey(key);
		configuredFragmentFetcher = new LdesFragmentFetcherImpl(config, HttpClients.createDefault());
		loadedConfig = configuredFragmentFetcher.config;

		assertEquals(header, loadedConfig.getApiKeyHeader());
		assertEquals(key, loadedConfig.getApiKey());

		// Properties config
		config = new LdesClientConfig(configWithApiKey);
		configuredFragmentFetcher = new LdesFragmentFetcherImpl(config, HttpClients.createDefault());
		loadedConfig = configuredFragmentFetcher.config;

		assertEquals(LdesClientDefaults.DEFAULT_API_KEY_HEADER, loadedConfig.getApiKeyHeader());
		assertEquals(configApiKey, loadedConfig.getApiKey());
	}

	@Test
	void whenFragmentUrlRedirects_thenFragmentIdWillBeSetToTargetUrl() {
		ldesService.setDataSourceFormat(Lang.JSONLD11);

		ldesService.queueFragment(initialFragmentUrl);

		LdesFragment fragment = ldesService.processNextFragment();

		assertEquals(actualFragmentUrl, fragment.getFragmentId());
	}

	@Test
	void whenExceptionOccursWhileFetchingFragment_thenUnparseableFragmentExceptionIsThrown() {
		fragmentFetcher.setDataSourceFormat(Lang.NQUADS);

		assertThrows(UnparseableFragmentException.class, () -> fragmentFetcher.fetchFragment(invalidFragmentUrl));
	}

	@Test
	void whenMaxAgeSet_thenFragmentFetcherSetsSetExpirationDate() {
		fragmentFetcher.setDataSourceFormat(Lang.JSONLD11);
		LdesFragment fragment = fragmentFetcher.fetchFragment(expirationDateSetFragmentUrl);

		assertNotNull(fragment.getExpirationDate());
		assertTrue(fragment.getExpirationDate().isAfter(LocalDateTime.now()));
	}

	@Test
	void whenMaxAgeNotSet_thenFragmentFetcherDoesntSetExpirationDate() {
		fragmentFetcher.setDataSourceFormat(Lang.JSONLD11);
		LdesFragment fragment = fragmentFetcher.fetchFragment(expirationDateNotSetFragmentUrl);

		assertNull(fragment.getExpirationDate());
	}

	@Test
	@DisplayName("Test fragment fetch with correct api key")
	void whenApiKeyRequired_andConfigHasApiKey_thenFragmentFetcherSendsApiKey_andFragmentCanBeFetched() {
		LdesFragmentFetcher fragmentFetcher;

		fragmentFetcher = getFragmentFetcher(LdesClientDefaults.DEFAULT_API_KEY_HEADER, apiKey);
		assertDoesNotThrow(() -> fragmentFetcher.fetchFragment(apiKeyFragmentUrl));

		verify(getRequestedFor(urlEqualTo("/api-key")).withHeader(LdesClientDefaults.DEFAULT_API_KEY_HEADER,
				equalTo(apiKey)));
	}

	@Test
	@DisplayName("Test fragment fetch without or with null api key")
	void whenNullApiKeySet_thenFragmentFetcherDoesntSendApiKey() {
		LdesFragmentFetcher nullApiKeyFragmentFetcher = getFragmentFetcher(LdesClientDefaults.DEFAULT_API_KEY_HEADER,
				null);
		assertThrows(FragmentFetcherException.class, () -> nullApiKeyFragmentFetcher.fetchFragment(apiKeyFragmentUrl));

		verify(getRequestedFor(urlEqualTo("/api-key")).withoutHeader(LdesClientDefaults.DEFAULT_API_KEY_HEADER));

		LdesFragmentFetcher emptyApiKeyFragmentFetcher = getFragmentFetcher(LdesClientDefaults.DEFAULT_API_KEY_HEADER,
				"");
		assertThrows(FragmentFetcherException.class, () -> emptyApiKeyFragmentFetcher.fetchFragment(apiKeyFragmentUrl));

		verify(getRequestedFor(urlEqualTo("/api-key")).withoutHeader(LdesClientDefaults.DEFAULT_API_KEY_HEADER));
	}

	private LdesFragmentFetcher getFragmentFetcher(String apiKeyHeader, String apiKey) {
		LdesClientConfig config = new LdesClientConfig();

		config.setApiKeyHeader(apiKeyHeader);
		config.setApiKey(apiKey);

		LdesFragmentFetcher fragmentFetcher = LdesClientImplFactory.getFragmentFetcher(config);

		fragmentFetcher.setDataSourceFormat(Lang.JSONLD);

		return fragmentFetcher;
	}

	@Test
	void whenHTTP304Response() {
		fragmentFetcher.setDataSourceFormat(Lang.JSONLD11);
		LdesFragment fragment = fragmentFetcher.fetchFragment(http304ResponseFragmentUrl);

		assertTrue(fragment.getModel().isIsomorphicWith(ModelFactory.createDefaultModel()));
	}
}
