package be.vlaanderen.informatievlaanderen.ldes.ldio.converter;

import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.datatypes.xsd.impl.XSDDateTimeType;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ValueConverterTest {
    private final ValueConverter converter = new ValueConverter();

    @Test
    void given_xsdDateTimeType_when_convert_then_returnConvertedOffsetDate() {
        XSDDateTime xsdDateTime = (XSDDateTime) new XSDDateTimeType("dateTime")
                .parseValidated("2024-12-18T13:00:40.575Z");
        Model model = ModelFactory.createDefaultModel();
        Literal literalDateTime = model.createTypedLiteral(xsdDateTime, new XSDDateTimeType("dateTime"));
        var dateTime = converter.convert(literalDateTime);
        OffsetDateTime expected = OffsetDateTime.parse("2024-12-18T13:00:40.575Z");
        assertThat(dateTime).isEqualTo(expected);
    }

    @Test
    void given_typedLiteralXsdDateTimeType_when_convert_then_returnConvertedLocalDateTime() {
        Model model = ModelFactory.createDefaultModel();
        Literal literalDateTime = model.createTypedLiteral("2021-01-06T17:11:43.167", "http://www.w3.org/2001/XMLSchema#dateTime");
        var dateTime = converter.convert(literalDateTime);
        LocalDateTime expected = LocalDateTime.parse("2021-01-06T17:11:43.167");
        assertThat(dateTime).isEqualTo(expected);
    }

    @Test
    void given_xsdDateTimeType_when_convert_then_returnConvertedLocalDateTime() {
        XSDDateTime xsdDateTime = (XSDDateTime) new XSDDateTimeType("dateTime")
                .parseValidated("2024-12-18T13:00:40.575");
        Model model = ModelFactory.createDefaultModel();
        Literal literalDateTime = model.createTypedLiteral(xsdDateTime, new XSDDateTimeType("dateTime"));
        var dateTime = converter.convert(literalDateTime);
        LocalDateTime expected = LocalDateTime.parse("2024-12-18T13:00:40.575");
        assertThat(dateTime).isEqualTo(expected);
    }

    @Test
    void given_xsdDateTimeType_when_convert_then_returnConvertedOffsetDateTimeWithCorrectTimezone() {
        XSDDateTime xsdDateTime = (XSDDateTime) new XSDDateTimeType("dateTime")
                .parseValidated("2024-12-18T13:00:40.575+06:00");
        Model model = ModelFactory.createDefaultModel();
        Literal literalDateTime = model.createTypedLiteral(xsdDateTime, new XSDDateTimeType("dateTime"));
        // Internally this xsdDateTime is converted to UTC.
        var dateTime = converter.convert(literalDateTime);
        OffsetDateTime expected = OffsetDateTime.parse("2024-12-18T07:00:40.575Z");
        assertThat(dateTime).isEqualTo(expected);
    }

    @Test
    void given_defaultRdfModelWithUri_when_convert_then_returnConvertedURI() {
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource("https://www.example.org/");
        var converted = converter.convert(resource);
        assertThat(converted).isEqualTo("https://www.example.org/");
    }

    @Test
    void given_defaultRdfModelWithBlankNode_when_convert_then_returnConvertedBlankNode() {
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource();
        var converted = converter.convert(resource);
        assertThat(converted).asString().startsWith("_:");
    }

    @Test
    void given_null_when_convert_then_returnNull() {
        var converted = converter.convert(null);
        assertThat(converted).isNull();
    }

}