package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldio.converter.ValueConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.repository.DbRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.repository.StatementCreationService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.sparqlselect.SparqlSelectService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LdioRdbOutTest {
    public static final String TABLE_NAME = "TEST_TABLE_NAME";
    public static final String SPARQL_SELECT_QUERY = """
            PREFIX ns: <https://test.org/ns#>
            PREFIX prov: <http://www.w3.org/ns/prov#>
            
            SELECT
                            ?SensorId ?GeneratedAtTime
            WHERE {
                 ?sensor ns:SensorId ?SensorId .
                 OPTIONAL { ?sensor prov:generatedAtTime ?GeneratedAtTime }
            }
            """;
    public static final String EXPECTED_INSERT_STATEMENT = "INSERT INTO TEST_TABLE_NAME (SensorId, GeneratedAtTime) VALUES (?, ?)";
    public static final OffsetDateTime EXPECTED_DATE_TIME = OffsetDateTime.parse("2024-12-18T13:00:40.575Z");
    public static final String EXPECTED_SENSOR_ID = "123456";
    private LdioRdbOut sut;
    private JdbcTemplate jdbcTemplateMock;

    @BeforeEach
    void setUp() {
        SparqlSelectService sparqlSelectService = new SparqlSelectService(new ValueConverter());
        jdbcTemplateMock = Mockito.mock(JdbcTemplate.class);
        StatementCreationService statementCreationService = new StatementCreationService();
        DbRepository dbRepository = new DbRepository(jdbcTemplateMock, statementCreationService, TABLE_NAME, true);
        sut = new LdioRdbOut(null,
                dbRepository,
                sparqlSelectService,
                TABLE_NAME,
                SPARQL_SELECT_QUERY,
                true);
    }

    @Test
    void given_emptyModel_when_accept_then_noRowsAreInserted() {
        Model model = ModelFactory.createDefaultModel();
        sut.accept(model);
        verify(jdbcTemplateMock, never()).update(any(), anyIterable());
    }

    @Test
    void given_modelWithOneRow_when_accept_then_updateIsExecuted() {
        RDFParser parser = RDFParser.create().source("one_sensor.ttl").build();
        Model model = parser.toModel();
        when(jdbcTemplateMock.update(eq(EXPECTED_INSERT_STATEMENT), eq(EXPECTED_SENSOR_ID), eq(EXPECTED_DATE_TIME))).thenReturn(1);
        sut.accept(model);
        verify(jdbcTemplateMock, times(1)).update(eq(EXPECTED_INSERT_STATEMENT), eq(EXPECTED_SENSOR_ID), eq(EXPECTED_DATE_TIME));
    }


    @Test
    void given_modelWithOneColumn_when_accept_then_updateIsExecuted() {
        RDFParser parser = RDFParser.create().source("one_sensor_with_one_column.ttl").build();
        Model model = parser.toModel();
        when(jdbcTemplateMock.update(eq(EXPECTED_INSERT_STATEMENT), eq(EXPECTED_SENSOR_ID))).thenReturn(1);
        sut.accept(model);
        verify(jdbcTemplateMock, times(1)).update(eq(EXPECTED_INSERT_STATEMENT), eq(EXPECTED_SENSOR_ID), eq(null));
    }


    @Test
    void given_modelWithTwoRows_when_accept_then_batchUpdateIsExecuted() {
        RDFParser parser = RDFParser.create().source("two_sensors.ttl").build();
        Model model = parser.toModel();

        when(jdbcTemplateMock.batchUpdate(eq(EXPECTED_INSERT_STATEMENT), anyList())).thenReturn(new int[]{1, 1});
        sut.accept(model);
        verify(jdbcTemplateMock, times(1)).batchUpdate(eq(EXPECTED_INSERT_STATEMENT), anyList());
    }
}