package be.vlaanderen.informatievlaanderen.ldes.ldi;

import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.SparqlProcessorProperties.SPARQL_SELECT_QUERY;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;
import static net.minidev.json.parser.JSONParser.DEFAULT_PERMISSIVE_MODE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SparqlSelectProcessorTest {
	private static final JSONParser parser = new JSONParser(DEFAULT_PERMISSIVE_MODE);
	private TestRunner testRunner;

	@BeforeEach
	public void setUp() {
		testRunner = TestRunners.newTestRunner(SparqlSelectProcessor.class);
	}

	@Test
	void testSuccessFlow_1() throws Exception {
		String selectQuery = """
				SELECT ?x ?fname ?gname
				WHERE {
				    ?x  <http://www.w3.org/2001/vcard-rdf/3.0#FN>  ?fname .
				    ?x  <http://www.w3.org/2001/vcard-rdf/3.0#N>/<http://www.w3.org/2001/vcard-rdf/3.0#Given> ?gname .
				}
				""";

		testSuccessFlow(selectQuery, "people_data.nq", "output_select_success_1.json");
	}

	@Test
	void testSuccessFlow_2() throws Exception {
		String selectQuery = """
				SELECT ?fname
				WHERE {
				    ?x  <http://www.w3.org/2001/vcard-rdf/3.0#FN>  ?fname .
				} LIMIT 2
				""";

		testSuccessFlow(selectQuery, "people_data.nq", "output_select_success_2.json");
	}

	@Test
	void testSuccessFlow_3() throws Exception {
		String selectQuery = """
				PREFIX mobiliteit: <https://data.vlaanderen.be/ns/mobiliteit#>
				SELECT ?subject
				WHERE {
				    ?subject mobiliteit:beheerder <https://private-api.gipod.test-vlaanderen.be/api/v1/organisations/9c5926df-195d-01be-a40a-360bcd21d662>
				}
				""";

		testSuccessFlow(selectQuery, "example-ldes-member.nq", "output_select_success_3.json");
	}

	@Test
	void testFailFlow() throws Exception {
		testRunner.setProperty(SPARQL_SELECT_QUERY, "INVALID QUERY");
		testRunner.enqueue(fileNameToFile("people_data.nq").toPath());

		testRunner.run();

		assertEquals(0, testRunner.getFlowFilesForRelationship(SUCCESS).size());
		testRunner.assertAllFlowFilesTransferred(FAILURE);
	}

	private void testSuccessFlow(String selectQuery, String inputFileName, String expectedResultFileName)
			throws Exception {
		testRunner.setProperty(SPARQL_SELECT_QUERY, selectQuery);
		testRunner.enqueue(fileNameToFile(inputFileName).toPath());

		testRunner.run();

		MockFlowFile result = testRunner.getFlowFilesForRelationship(SUCCESS).get(0);
		verifyJsonArrayStrings(result.getContent(), expectedResultFileName);
		assertEquals("application/json", result.getAttribute("mime.type"));
		testRunner.assertAllFlowFilesTransferred(SUCCESS);
	}

	private void verifyJsonArrayStrings(String actual, String expectedResultFileName) throws Exception {
		JSONArray a = toJsonArray(new FileReader(fileNameToFile(expectedResultFileName).getPath()));
		JSONArray b = toJsonArray(actual);
		assertEquals(a, b);
	}

	private File fileNameToFile(String fileName) throws URISyntaxException {
		return new File(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).toURI());
	}

	static JSONArray toJsonArray(FileReader fileReader) throws ParseException {
		return (JSONArray) parser.parse(fileReader);
	}

	static JSONArray toJsonArray(String s) throws ParseException {
		return (JSONArray) parser.parse(s);
	}

}
