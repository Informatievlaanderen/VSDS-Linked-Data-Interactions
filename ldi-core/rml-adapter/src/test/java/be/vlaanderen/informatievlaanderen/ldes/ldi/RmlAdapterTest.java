package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RmlAdapterTest {
	@Test
	void test_smartphonesMapping() {
		var models = runRmlTest("smartphones/data.json", "smartphones/mapping.ttl", "application/json");

		assertEquals(5, models.size());
	}

	@Test
	void test_userCarts() {
		var models = runRmlTest("usercarts/data.json", "usercarts/mapping.ttl", "application/json");

		assertEquals(20, models.size());
	}

	@Test
	void test_awv_location() {
		var models = runRmlTest("awv/location/data.xml", "awv/location/mapping.ttl", "application/xml");

		var expected = RDFParser.source("awv/location/expected.nt").lang(Lang.NQUADS).toModel();

		assertEquals(1, models.size());
		assertTrue(models.get(0).isIsomorphicWith(expected));
	}

	@Test
	void test_awv_observation() {
		var models = runRmlTest("awv/observation/data.xml", "awv/observation/mapping.ttl", "application/xml");

		assertEquals(1, models.size());
	}

	private List<Model> runRmlTest(String dataPath, String mappingPath, String mimeType) {
		RmlAdapter rmlAdapter = new RmlAdapter(getFileContent(mappingPath));

		List<Model> models = rmlAdapter.apply(LdiAdapter.Content.of(getFileContent(
				dataPath), mimeType)).toList();

		models.forEach(model -> {
			System.out.println("New member");
			RDFWriter.source(model).lang(Lang.TTL).output(System.out);
		});

		return models;
	}

	private String getFileContent(String fileName) {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
			return FileUtils.readFileToString(file, "UTF-8");
		} catch (NullPointerException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}
