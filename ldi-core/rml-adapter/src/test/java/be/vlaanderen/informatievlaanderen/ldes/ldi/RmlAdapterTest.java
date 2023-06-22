package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import io.carml.model.TriplesMap;
import io.carml.util.RmlMappingLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RmlAdapterTest {
	@Test
	void test_smartphonesMapping() throws IOException {
		var models = runRmlTest("smartphones/data.json", "smartphones/mapping.ttl", "application/json");

		assertEquals(5, models.size());
	}

	@Test
	void test_userCarts() throws IOException {
		var models = runRmlTest("usercarts/data.json", "usercarts/mapping.ttl", "application/json");

		assertEquals(20, models.size());
	}

	@Test
	void test_awv_location() throws IOException {
		var models = runRmlTest("awv/location/data.xml", "awv/location/mapping.ttl", "application/xml");

		var expected = RDFParser.source("awv/location/expected.nt").lang(Lang.NQUADS).toModel();

		assertEquals(1, models.size());
		assertTrue(models.get(0).isIsomorphicWith(expected));
	}

	@Test
	void test_awv_observation() throws IOException {
		var models = runRmlTest("awv/observation/data.xml", "awv/observation/mapping.ttl", "application/xml");

//		var expected = RDFParser.source("awv/observation/expected.nt").lang(Lang.NQUADS).toModel();

		assertEquals(1, models.size());
//		assertTrue(models.get(0).isIsomorphicWith(expected));
	}

	private List<org.apache.jena.rdf.model.Model> runRmlTest(String dataPath, String mappingPath, String mimeType) throws IOException {
		String mappingString = getFileContent(mappingPath);

		Set<TriplesMap> mapping = RmlMappingLoader.build()
				.load(convertToCarmlMappingModel(mappingString));

		RmlAdapter rmlAdapter = new RmlAdapter(mapping);

		List<org.apache.jena.rdf.model.Model> models = rmlAdapter.apply(LdiAdapter.Content.of(getFileContent(
				dataPath), mimeType)).toList();

		models.forEach(model -> {
			System.out.println("New member");
			RDFWriter.source(model).lang(Lang.TTL).output(System.out);
		});

		return models;
	}

	private Model convertToCarmlMappingModel(String mappingString) throws IOException {
		Model mappingModel = Rio.parse(IOUtils.toInputStream(mappingString), RDFFormat.TURTLE);

		ValueFactory vf = SimpleValueFactory.getInstance();
		var logicalSourceStmts = mappingModel.getStatements(null, RDF.TYPE,
				vf.createIRI("http://semweb.mmlab.be/ns/rml#LogicalSource"));

		List<Resource> sourcesToClean = new ArrayList<>();
		List<Statement> newStatements = new ArrayList<>();

		for (Statement logicalSourceStmt : logicalSourceStmts) {
			Resource sourceSubject = logicalSourceStmt.getSubject();
			sourcesToClean.add(sourceSubject);

			Statement statement = vf.createStatement(vf.createBNode(), RDF.TYPE,
					vf.createIRI("http://carml.taxonic.com/carml/", "Stream"));
			newStatements.add(vf.createStatement(sourceSubject, vf.createIRI("http://semweb.mmlab.be/ns/rml#source"),
					statement.getSubject()));
			newStatements.add(statement);
		}

		sourcesToClean.forEach(source -> {
			mappingModel.remove(source, RDF.TYPE, vf.createIRI("http://semweb.mmlab.be/ns/rml#LogicalSource"));
			mappingModel.remove(source, vf.createIRI("http://semweb.mmlab.be/ns/rml#source"), null);
		});
		mappingModel.addAll(newStatements);

		return mappingModel;
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
