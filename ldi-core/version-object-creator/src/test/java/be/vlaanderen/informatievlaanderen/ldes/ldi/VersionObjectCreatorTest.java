package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.EmptyPropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.MemberInfo;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.VersionObjectCreator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class VersionObjectCreatorTest {
	private static final String DEFAULT_DELIMITER = "/";

	private static final Model initModel = ModelFactory.createDefaultModel();
	private static final Property PROV_GENERATED_AT_TIME = initModel
			.createProperty("http://www.w3.org/ns/prov#generatedAtTime");
	private static final Property TERMS_IS_VERSION_OF = initModel
			.createProperty("http://purl.org/dc/terms/isVersionOf");
	private static final String WATER_QUALITY_OBSERVED = "https://uri.etsi.org/ngsi-ld/default-context/WaterQualityObserved";

	MemberInfo memberInfo = new MemberInfo(
			"urn:ngsi-v2:cot-imec-be:WaterQualityObserved:imec-iow-3orY3reQDK5n3TMpPnLVYR", "2022-04-19T11:40:42.000Z");

	@Test
	void when_constructVersionObject_expectLdesProperties() throws URISyntaxException, IOException {
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

	@Test
	void when_dateObservedPropertyIsNoDatetime_expectCurrentDatetime() {
		Model inputModel = RDFParser.fromString("""
				@prefix ex:   <http://example.org/> .

				ex:member
				  a ex:Something ;
				  ex:foo "bar mitswa".
				""").lang(Lang.TTL).toModel();

		String expectedId = "http://example.org/member/";

		VersionObjectCreator versionObjectCreator = createVersionObjectCreator(
				inputModel,
				"<http://example.org/foo>"
		);

		Model versionObject = versionObjectCreator.transform(inputModel);

		final LocalDateTime startTestTime = LocalDateTime.now();

		final String minuteTheTestStarted = getPartOfLocalDateTime(startTestTime);
		final String minuteAfterTheTestStarted = getPartOfLocalDateTime(startTestTime.plusMinutes(1));

		assertTrue(versionObject.listStatements()
				.toList()
				.stream()
				.anyMatch(stmt -> stmt.getSubject().toString().contains(expectedId + minuteTheTestStarted) ||
						stmt.getSubject().toString().contains(expectedId + minuteAfterTheTestStarted)));
	}

	@Test
	void when_dateObservedPropertyIsNested_thenAPropertyPathCanBeProvided() {
		Model inputModel = RDFParser.fromString("""
				@prefix time: <http://www.w3.org/2006/time#> .
				@prefix ex:   <http://example.org/> .
				@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

				ex:member
				  a ex:Something ;
				  ex:created [
				    a time:Instant ;
				    time:inXSDDateTimeStamp "2023-08-18T13:08:00+01:00"^^xsd:DateTime
				  ] .
				""").lang(Lang.TTL).toModel();

		VersionObjectCreator versionObjectCreator = createVersionObjectCreator(
				inputModel,
				"<http://example.org/created>/<http://www.w3.org/2006/time#inXSDDateTimeStamp>"
		);

		String result = versionObjectCreator
				.transform(inputModel)
				.listSubjectsWithProperty(RDF.type, initModel.createResource("http://example.org/Something"))
				.mapWith(RDFNode::asResource)
				.mapWith(Resource::getURI)
				.mapWith(String::toString)
				.next();

		assertEquals("http://example.org/member/2023-08-18T13:08:00+01:00", result);
	}

	@ParameterizedTest
	@ArgumentsSource(JsonLDFileArgumentsProvider.class)
	void shouldMatchCountOfObjects(String fileName, String expectedId, LocalDateTime startTestTime, List<String> memberTypes)
			throws IOException, URISyntaxException {

		Model model = RDFParserBuilder.create().fromString(getJsonString(fileName)).lang(Lang.JSONLD).toModel();

		VersionObjectCreator versionObjectCreator = new VersionObjectCreator(new EmptyPropertyExtractor(),
				memberTypes.stream().map(model::createResource).toList(),
				DEFAULT_DELIMITER, null, null);

		Model versionObject = versionObjectCreator.transform(model);

		final String minuteTheTestStarted = getPartOfLocalDateTime(startTestTime);
		final String minuteAfterTheTestStarted = getPartOfLocalDateTime(startTestTime.plusMinutes(1));
		assertTrue(versionObject.listStatements()
				.toList()
				.stream()
				.anyMatch(stmt -> stmt.getSubject().toString().contains(expectedId + minuteTheTestStarted) ||
						stmt.getSubject().toString().contains(expectedId + minuteAfterTheTestStarted)));
	}

	@Test
	void when_EmptyList_Then_ShouldNotMatch() throws URISyntaxException, IOException {
		final LocalDateTime now = LocalDateTime.now();
		Model model = RDFParserBuilder.create().fromString(getJsonString("example-waterqualityobserved.json")).lang(Lang.JSONLD).toModel();
		VersionObjectCreator versionObjectCreator = new VersionObjectCreator(new EmptyPropertyExtractor(),
				List.of(),
				DEFAULT_DELIMITER, null, null);

		Model versionObject = versionObjectCreator.transform(model);

		final String minuteTheTestStarted = getPartOfLocalDateTime(now);
		final String minuteAfterTheTestStarted = getPartOfLocalDateTime(now.plusMinutes(1));
		assertTrue(versionObject.listStatements()
				.toList()
				.stream()
				.noneMatch(stmt -> stmt.getSubject().toString().contains(minuteTheTestStarted) ||
						stmt.getSubject().toString().contains(minuteAfterTheTestStarted)));
	}

	@Test
	void when_memberInfoExtractionFails_warningMessageIsLogged() {
		ListAppender<ILoggingEvent> listAppender = createListAppender();
		Model inputModel = RDFParser.fromString("""
				@prefix ex: <http://example.org/> .
				@prefix time: <http://www.w3.org/2006/time#> .
				@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
				
				ex:John
					ex:hasAge "30" ;
					ex:hasName "John Smith" ;
					ex:hasFriend ex:Jane ;
					ex:created [
				    	    a time:Instant ;
				    	    time:inXSDDateTimeStamp "2023-08-18T13:08:00+01:00"^^xsd:DateTime
				  	] .
				""").lang(Lang.TTL).toModel();

		VersionObjectCreator versionObjectCreator = createVersionObjectCreator(
				inputModel,
				"<http://example.org/created>/<http://www.w3.org/2006/time#inXSDDateTimeStamp>"
		);

		versionObjectCreator.transform(inputModel);

		assertExpectedLogs(listAppender.list, STATEMENT_NOT_FOUND);
	}

	@Test
	void when_dateObservedExtractionFails_warningMessageIsLogged() {
		ListAppender<ILoggingEvent> listAppender = createListAppender();
		Model inputModel = RDFParser.fromString("""
				@prefix time: <http://www.w3.org/2006/time#> .
				@prefix ex:   <http://example.org/> .
				@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

				ex:member
				  a ex:Something .
				""").lang(Lang.TTL).toModel();

		VersionObjectCreator versionObjectCreator = createVersionObjectCreator(
				inputModel,
				"<http://example.org/created>/<http://www.w3.org/2006/time#inXSDDateTimeStamp>"
		);
		versionObjectCreator.transform(inputModel);

		assertExpectedLogs(listAppender.list, DATE_OBSERVED_PROPERTY_COULD_NOT_BE_FOUND, CREATED_VERSION);
	}

	@Test
	void when_modelIsEmpty_warningMessagesAreLogged() {
		ListAppender<ILoggingEvent> listAppender = createListAppender();

		VersionObjectCreator versionObjectCreator = new VersionObjectCreator(new EmptyPropertyExtractor(), null,
				DEFAULT_DELIMITER,
				null, null);
		versionObjectCreator.transform(ModelFactory.createDefaultModel());

		assertExpectedLogs(listAppender.list, LINKED_DATA_MODEL_IS_EMPTY, DATE_OBSERVED_PROPERTY_COULD_NOT_BE_FOUND);
	}

	private static void assertExpectedLogs(List<ILoggingEvent> logsList, String... expectedMessages) {
		assertThat(logsList)
				.extracting(ILoggingEvent::getMessage)
				.containsExactlyInAnyOrder(expectedMessages);
	}

	private String getPartOfLocalDateTime(LocalDateTime time) {
		return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:"));
	}

	private String getJsonString(String resource) throws URISyntaxException, IOException {
		File file = new File(
				Objects.requireNonNull(getClass().getClassLoader().getResource(resource)).toURI());
		return Files.readString(file.toPath());
	}

	private VersionObjectCreator createVersionObjectCreator(Model inputModel, String dateObservedPath) {
		List<Resource> memberTypes = List.of(inputModel.createResource("http://example.org/Something"));
		PropertyExtractor dateObservedPropertyExtractor = PropertyPathExtractor.from(dateObservedPath);
		Property generatedAtTimeProperty = inputModel.createProperty("http://www.w3.org/ns/prov#generatedAtTime");
		Property versionOfProperty = inputModel.createProperty("http://purl.org/dc/terms/isVersionOf");

		return new VersionObjectCreator(
				dateObservedPropertyExtractor,
				memberTypes,
				DEFAULT_DELIMITER,
				generatedAtTimeProperty,
				versionOfProperty
		);
	}

	private ListAppender<ILoggingEvent> createListAppender() {
		Logger vocLogger = (Logger) LoggerFactory.getLogger(VersionObjectCreator.class);
		ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
		listAppender.start();
		vocLogger.addAppender(listAppender);
		return listAppender;
	}

	static class JsonLDFileArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			final LocalDateTime now = LocalDateTime.now();
			return Stream.of(
					Arguments.of("example-waterqualityobserved.json",
							"urn:ngsi-v2:cot-imec-be:WaterQualityObserved:imec-iow-3orY3reQDK5n3TMpPnLVYR/",
							now,
							List.of("https://uri.etsi.org/ngsi-ld/default-context/WaterQualityObserved")),
					Arguments.of("example-device.json",
							"urn:ngsi-v2:cot-imec-be:Device:imec-iow-UR5gEycRuaafxnhvjd9jnU/",
							now,
							List.of("https://uri.etsi.org/ngsi-ld/default-context/Device")),
					Arguments.of("example-device-model.json",
							"urn:ngsi-v2:cot-imec-be:devicemodel:imec-iow-sensor-v0005/",
							now,
							List.of("https://uri.etsi.org/ngsi-ld/default-context/DeviceModel")),
					Arguments.of("example-device-model.json",
							"urn:ngsi-v2:cot-imec-be:devicemodel:imec-iow-sensor-v0005/",
							now,
							List.of("https://www.test.org",
									"https://uri.etsi.org/ngsi-ld/default-context/DeviceModel",
									"https://www.something.org")));
		}
	}

}
