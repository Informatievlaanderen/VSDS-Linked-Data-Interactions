package be.vlaanderen.informatievlaanderen.ldes.client.services;

import be.vlaanderen.informatievlaanderen.ldes.client.LdesClientImplFactory;
import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesFragment;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.DEFAULT_DATA_SOURCE_FORMAT;
import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.DEFAULT_FRAGMENT_EXPIRATION_INTERVAL;
import static be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesConstants.W3ID_TREE_NODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
@WireMockTest(httpPort = LdesServiceImplTest.HTTP_PORT)
class LdesServiceImplTest {

	public static final int HTTP_PORT = 10101;

	private final String initialFragmentUrl = "http://localhost:" + HTTP_PORT
			+ "/exampleData?generatedAtTime=2022-05-03T00:00:00.000Z";
	private final String initialFragmentRelationUrl = "http://localhost:" + HTTP_PORT
			+ "/exampleData?generatedAtTime=2022-05-04T00:00:00.000Z";
	private final String oneMemberFragmentUrl = "http://localhost:" + HTTP_PORT
			+ "/exampleData?generatedAtTime=2022-05-05T00:00:00.000Z";
	private final String oneMemberUrl = "http://localhost:" + HTTP_PORT
			+ "/member?generatedAtTime=2022-05-05T00:00:00.000Z";

	private final Long negativeFragmentExpirationInterval = -5L;

	private LdesService ldesService = LdesClientImplFactory.getLdesService();

	@BeforeEach
	void setup() {
		ldesService.getStateManager().clearState();
	}

	@AfterAll
	void tearDown() {
		ldesService.getStateManager().destroyState();
	}

	@Test
	void when_processRelations_expectFragmentQueueToBeUpdated() {
		ldesService.setDataSourceFormat(Lang.JSONLD11);
		ldesService.queueFragment(initialFragmentUrl);

		assertEquals(1, ldesService.getStateManager().countQueuedFragments());

		ldesService.extractRelations(getInputModelFromUrl(initialFragmentUrl))
				.forEach(relationStatement -> ldesService.getStateManager().queueFragment(
						relationStatement.getResource().getProperty(W3ID_TREE_NODE).getResource().toString()));

		assertEquals(2, ldesService.getStateManager().countQueuedFragments());
	}

	@Test
	void when_ProcessNextFragmentWith2Fragments_expect2MembersPerFragment() {
		ldesService.setDataSourceFormat(Lang.JSONLD11);
		ldesService.queueFragment(initialFragmentUrl);

		LdesFragment fragment;

		assertEquals(1, ldesService.getStateManager().countQueuedFragments());
		fragment = ldesService.processNextFragment();
		assertEquals(initialFragmentUrl, fragment.getFragmentId());
		assertEquals(2, fragment.getMembers().size());

		assertEquals(1, ldesService.getStateManager().countQueuedFragments());
		fragment = ldesService.processNextFragment();
		assertEquals(initialFragmentRelationUrl, fragment.getFragmentId());
		assertEquals(2, fragment.getMembers().size());
	}

	@Test
	void when_ProcessNextFragment_expectValidLdesMember() {
		ldesService.setDataSourceFormat(Lang.JSONLD11);

		ldesService.queueFragment(oneMemberFragmentUrl);

		LdesFragment fragment = ldesService.processNextFragment();

		assertEquals(oneMemberFragmentUrl, fragment.getFragmentId());
		assertEquals(1, fragment.getMembers().size());

		Model outputModel = fragment.getMembers().get(0).getMemberModel();
		Model validateModel = getInputModelFromUrl(oneMemberUrl);

		assertTrue(outputModel.isIsomorphicWith(validateModel));
	}

	// @Test
	// void when_CreatingServiceWithNullParameters_expectValidLdesMember() {
	// assertEquals(DEFAULT_DATA_SOURCE_FORMAT, ldesService.getDataSourceFormat());
	// assertEquals(DEFAULT_FRAGMENT_EXPIRATION_INTERVAL,
	// ldesService.getFragmentExpirationInterval());
	// }

	@Test
	void when_CreatingServiceWithNegativeFragmentExpirationValue_expectValidLdesMember() {
		ldesService.setDataSourceFormat(DEFAULT_DATA_SOURCE_FORMAT);
		assertEquals(DEFAULT_FRAGMENT_EXPIRATION_INTERVAL, ldesService.getFragmentExpirationInterval());

		ldesService.setFragmentExpirationInterval(negativeFragmentExpirationInterval);
		assertEquals(negativeFragmentExpirationInterval, ldesService.getFragmentExpirationInterval());
	}

	private Model getInputModelFromUrl(String fragmentUrl) {
		Model inputModel = ModelFactory.createDefaultModel();

		RDFParser.source(fragmentUrl).forceLang(Lang.JSONLD11).parse(inputModel);

		return inputModel;
	}
}
