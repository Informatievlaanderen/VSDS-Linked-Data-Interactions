package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import io.carml.model.TriplesMap;
import io.carml.util.RmlMappingLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.Lang;
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

public class RmlAdapterTest {
	@Test
	void test_smartphonesMapping() throws IOException {
		String mappingString = getFileContent("smartphones/mapping.ttl");

		Set<TriplesMap> mapping = RmlMappingLoader.build()
				.load(convertToCarmlMappingModel(mappingString));

		RmlAdapter rmlAdapter = new RmlAdapter(mapping);

		List<org.apache.jena.rdf.model.Model> models = rmlAdapter.apply(LdiAdapter.Content.of(getFileContent(
				"smartphones/data.json"), "application/json")).toList();

		models.forEach(model -> {
			System.out.println("new member");
			RDFWriter.source(model).lang(Lang.TTL).output(System.out);
		});

		assertEquals(5, models.size());
	}

	@Test
	void test_userCarts() throws IOException {
		String mappingString = getFileContent("usercarts/mapping.ttl");

		Set<TriplesMap> mapping = RmlMappingLoader.build()
				.load(convertToCarmlMappingModel(mappingString));

		RmlAdapter rmlAdapter = new RmlAdapter(mapping);

		List<org.apache.jena.rdf.model.Model> models = rmlAdapter.apply(LdiAdapter.Content.of(getFileContent(
				"usercarts/data.json"), "application/json")).toList();

		models.forEach(model -> {
			System.out.println("new member");
			RDFWriter.source(model).lang(Lang.TTL).output(System.out);
		});

		assertEquals(20, models.size());
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
