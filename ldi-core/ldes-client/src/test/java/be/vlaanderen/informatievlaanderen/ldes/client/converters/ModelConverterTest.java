package be.vlaanderen.informatievlaanderen.ldes.client.converters;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.client.converters.ModelConverter.*;
import static be.vlaanderen.informatievlaanderen.ldes.client.converters.ModelConverter.convertStringToModel;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelConverterTest {

	private final String[] members = new String[] { "member1.nq", "member2.nq", "member3.nq" };
	private final String[] fragments = new String[] { "fragment1.json" };

	@Test
	void whenConvertingMemberModel_thenResultingOutputCanBeConvertedBack() throws Exception {
		Lang lang = Lang.NQUADS;

		for (String member : members) {
			Model originalModel = getModel(member, lang);
			Model reconvertedModel = convert(originalModel, lang);

			assertTrue(reconvertedModel.isIsomorphicWith(originalModel));
		}
	}

	@Test
	void whenConvertingFragmentModel_thenResultingOutputCanBeConvertedBack() throws Exception {
		Lang lang = Lang.JSONLD11;

		for (String fragment : fragments) {
			Model originalModel = getModel(fragment, lang);
			Model convertedModel = convert(originalModel, Lang.NQUADS);

			String o = convertModelToString(originalModel, lang);
			String c = convertModelToString(convertedModel, lang);

			// to be sure
			assertTrue(convertStringToModel(c, lang).isIsomorphicWith(getModelFromString(o, lang)));
			// from models
			assertTrue(convertedModel.isIsomorphicWith(originalModel));
		}
	}

	@Test
	void whenConvertingIsomorphicModels_thenComparingResultingOutputIsAlsoIsomorphic() throws Exception {
		Model jsonld11Model = getModel("fragment1.json", Lang.JSONLD11);
		Model nquadsModel = getModel("fragment1.nq", Lang.NQUADS);

		// to be sure
		assertTrue(nquadsModel.isIsomorphicWith(jsonld11Model));

		Model convertedJsonLd11Model = convert(jsonld11Model, Lang.NQUADS);
		Model convertedNquadsModel = convert(nquadsModel, Lang.NQUADS);

		// from models
		assertTrue(convertedJsonLd11Model.isIsomorphicWith(convertedNquadsModel));
	}

	private Model getModel(String dataFile, Lang lang) throws Exception {
		Model model = ModelFactory.createDefaultModel();

		RDFParser.source(Paths.get(String.valueOf(
				new File(Objects.requireNonNull(getClass().getClassLoader().getResource(dataFile)).toURI()))))
				.lang(lang)
				.parse(model);

		return model;
	}

	private Model getModelFromString(String data, Lang lang) {
		Model model = ModelFactory.createDefaultModel();

		RDFParser.fromString(data)
				.lang(lang)
				.parse(model);

		return model;
	}

	private Model convert(Model model, Lang lang) {
		return convertStringToModel(convertModelToString(model, lang), lang);
	}
}
