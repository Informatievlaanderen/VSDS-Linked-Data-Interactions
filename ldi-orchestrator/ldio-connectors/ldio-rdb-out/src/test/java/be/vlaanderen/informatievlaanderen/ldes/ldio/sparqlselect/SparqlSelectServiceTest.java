package be.vlaanderen.informatievlaanderen.ldes.ldio.sparqlselect;

import be.vlaanderen.informatievlaanderen.ldes.ldio.converter.ValueConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.dto.ColumnsDTO;
import be.vlaanderen.informatievlaanderen.ldes.ldio.dto.DataModelDTO;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SparqlSelectServiceTest {
    public static final String SPARQL_QUERY = """
            PREFIX ldes: <https://w3id.org/ldes#>
            PREFIX ns: <https://test.org/ns#>
            PREFIX prov: <http://www.w3.org/ns/prov#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            PREFIX sensor: <https://test.org/id/sensor/>
            PREFIX sosa: <http://www.w3.org/ns/sosa/>
            PREFIX terms: <http://purl.org/dc/terms/>
            PREFIX tree: <https://w3id.org/tree#>
            PREFIX wgs84_pos: <http://www.w3.org/2003/01/geo/wgs84_pos#>
            
            SELECT
                ?isVersionOf ?lat_Lambert72 ?lat_WGS84 ?long_Lambert72 ?long_WGS84 ?GeneratedAtTime ?hasFeatureOfInterest ?SensorId ?brand ?deviceState ?name ?owner ?serialNumber ?supplier
            WHERE {
            ?sensor
                terms:isVersionOf ?isVersionOf ;
                wgs84_pos:lat_Lambert72 ?lat_Lambert72 ;
                wgs84_pos:lat_WGS84 ?lat_WGS84 ;
                wgs84_pos:long_Lambert72 ?long_Lambert72 ;
                wgs84_pos:long_WGS84 ?long_WGS84 ;
                prov:generatedAtTime ?GeneratedAtTime ;
                sosa:hasFeatureOfInterest ?hasFeatureOfInterest ;
                ns:SensorId  ?SensorId ;
                ns:brand ?brand ;
                ns:deviceState ?deviceState ;
                ns:name ?name ;
                ns:owner ?owner ;
                ns:serialNumber ?serialNumber ;
                ns:supplier ?supplier ;
            }
            """;
    private SparqlSelectService sparqlSelectService;

    private static @NotNull DataModelDTO getDataModelDTO() {
        return new DataModelDTO(new ColumnsDTO(List.of("isVersionOf", "lat_Lambert72", "lat_WGS84", "long_Lambert72", "long_WGS84", "GeneratedAtTime", "hasFeatureOfInterest", "SensorId", "brand", "deviceState", "name", "owner", "serialNumber", "supplier")));
    }

    private static Model getModelFromTtlFile() {
        RDFParser parser = RDFParser.create().source("two_sensors.ttl").build();
        return parser.toModel();
    }

    @BeforeEach
    public void setUp() {
        ValueConverter valueConverter = new ValueConverter();
        sparqlSelectService = new SparqlSelectService(valueConverter);
    }

    @Test
    public void when_executeWithNullModel_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> sparqlSelectService.execute(null, SPARQL_QUERY, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void given_incorrectSparqlQuery_when_execute_then_throwsQueryParseException() {
        Model model = getModelFromTtlFile();
        DataModelDTO dataModelDTO = getDataModelDTO();

        assertThatThrownBy(() -> sparqlSelectService.execute(model, "incorrect SPARQL Select query", dataModelDTO)).isInstanceOf(QueryParseException.class);
    }

    @Test
    public void given_correctSparqlQuery_when_execute_then_returnsCorrectResult() {
        Model model = getModelFromTtlFile();
        DataModelDTO dataModelDTO = getDataModelDTO();
        DataModelDTO result = sparqlSelectService.execute(model, SPARQL_QUERY, dataModelDTO);
        assertThat(result.getData().values().get(1))
                .hasSize(14)

                .containsExactly("https://test.org/id/sensor/329fe5af-6656-f13e-0a81-1898a6bf5f6a",
                        "132776",
                        "50.987654",
                        "186408",
                        "4.123456",
                        OffsetDateTime.parse("2024-12-18T13:00:40.575Z"),
                        "K_000000123456",
                        "123456",
                        "Acme",
                        "Active",
                        "Acme-123456",
                        "Trunyx",
                        "123456789123",
                        "Quatz");
        assertThat(result.getData().values().get(0))
                .hasSize(14)
                .containsExactly("https://test.org/id/sensor/b38fdbda-2cbe-c56f-e79f-9ae549a80c66",
                        "140556",
                        "50.876543",
                        "174028",
                        "4.234567",
                        OffsetDateTime.parse("2024-12-18T13:00:40.575Z"),
                        "A_000000123456",
                        "654321",
                        "Acme",
                        "Active",
                        "Acme-654321",
                        "Trunyx",
                        "987654321987",
                        "Quatz");
    }
}