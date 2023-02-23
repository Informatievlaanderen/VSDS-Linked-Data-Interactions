package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionObjectCreator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.MemberInfo;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.VersionObjectCreator.SYNTAX_TYPE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LdesMemberConverterTest {
	private static final String DEFAULT_DELIMITER = "/";

	private static final Model initModel = ModelFactory.createDefaultModel();
	private static final Property PROV_GENERATED_AT_TIME = initModel.createProperty(
			"http://www.w3.org/ns/prov#generatedAtTime");
	private static final Property TERMS_IS_VERSION_OF = initModel.createProperty(
			"http://purl.org/dc/terms/isVersionOf");
	private static final String WATER_QUALITY_OBSERVED = "https://uri.etsi.org/ngsi-ld/default-context/WaterQualityObserved";

	MemberInfo memberInfo = new MemberInfo(
			"urn:ngsi-v2:cot-imec-be:WaterQualityObserved:imec-iow-3orY3reQDK5n3TMpPnLVYR", "2022-04-19T11:40:42.000Z");

	@Test
	void when_constructVersionObject_ExpectLdesProperties() throws URISyntaxException, IOException {
		Model model = RDFParserBuilder.create().fromString(getJsonString("outputformat/example-ldes.json"))
				.lang(Lang.JSONLD).toModel();

		VersionObjectCreator versionObjectCreator = new VersionObjectCreator(null, null, DEFAULT_DELIMITER,
				PROV_GENERATED_AT_TIME, TERMS_IS_VERSION_OF);

		Model actualOutput = versionObjectCreator.constructVersionObject(model, memberInfo);

		assertFalse(actualOutput.listStatements(null, PROV_GENERATED_AT_TIME,
				model.createTypedLiteral(memberInfo.getObservedAt(), "http://www.w3.org/2001/XMLSchema#dateTime"))
				.toList().isEmpty());
		assertFalse(actualOutput.listStatements(null, SYNTAX_TYPE,
				model.createResource(WATER_QUALITY_OBSERVED)).toList().isEmpty());
		assertFalse(
				actualOutput.listStatements(null, TERMS_IS_VERSION_OF, model.createResource(memberInfo.getVersionOf()))
						.toList().isEmpty());
	}

	@ParameterizedTest
	@ArgumentsSource(JsonLDFileArgumentsProvider.class)
	void shouldMatchCountOfObjects(String fileName, String expectedId, String memberType)
			throws IOException, URISyntaxException {

		Model model = RDFParserBuilder.create().fromString(getJsonString(fileName)).lang(Lang.JSONLD).toModel();

		VersionObjectCreator versionObjectCreator = new VersionObjectCreator(null, model.createResource(memberType),
				DEFAULT_DELIMITER, null, null);

		Model versionObject = versionObjectCreator.transform(model);

		assertTrue(
				versionObject.listStatements()
						.toList()
						.stream()
						.anyMatch(stmt -> stmt.getSubject().toString().contains(expectedId)));
	}

	private String getJsonString(String resource) throws URISyntaxException, IOException {
		File file = new File(
				Objects.requireNonNull(getClass().getClassLoader().getResource(resource)).toURI());
		return Files.readString(file.toPath());
	}

	static class JsonLDFileArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("example-waterqualityobserved.json",
							"urn:ngsi-v2:cot-imec-be:WaterQualityObserved:imec-iow-3orY3reQDK5n3TMpPnLVYR/"
									+ getPartOfLocalDateTime(),
							"https://uri.etsi.org/ngsi-ld/default-context/WaterQualityObserved"),
					Arguments.of("example-device.json",
							"urn:ngsi-v2:cot-imec-be:Device:imec-iow-UR5gEycRuaafxnhvjd9jnU/"
									+ getPartOfLocalDateTime(),
							"https://uri.etsi.org/ngsi-ld/default-context/Device"),
					Arguments.of("example-device-model.json",
							"urn:ngsi-v2:cot-imec-be:devicemodel:imec-iow-sensor-v0005/" + getPartOfLocalDateTime(),
							"https://uri.etsi.org/ngsi-ld/default-context/DeviceModel"));
		}

		private String getPartOfLocalDateTime() {
			return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:"));
		}
	}

}
