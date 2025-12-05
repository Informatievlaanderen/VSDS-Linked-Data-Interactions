package be.vlaanderen.informatievlaanderen.ldes.ldio.repository;

import be.vlaanderen.informatievlaanderen.ldes.ldio.DbContainerExtension;
import be.vlaanderen.informatievlaanderen.ldes.ldio.dto.ColumnsDTO;
import be.vlaanderen.informatievlaanderen.ldes.ldio.dto.DataModelDTO;
import be.vlaanderen.informatievlaanderen.ldes.ldio.dto.ValuesDTO;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@JdbcTest
@ExtendWith(DbContainerExtension.class)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Tag("integration")
class DbRepositoryIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private StatementCreationService statementCreationService;

    @Test
    @Sql("/db/sensor-schema.sql")
    void given_emptyDatabase_when_insertSensor_then_sensorGetsInserted() {
        DbRepository dbRepository = new DbRepository(jdbcTemplate, statementCreationService, "sensor_test", true);
        List<String> columns = List.of("sensor_id", "latitude", "longitude", "generated_at_time");
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2024-12-18T13:00:40.575Z");
        List<Object> values = List.of("123456", "50.987654", "4.123456", offsetDateTime);
        DataModelDTO dataModelDTO = new DataModelDTO(new ColumnsDTO(columns));
        dataModelDTO.setData(new ValuesDTO(List.of(values)));
        int insertCount = dbRepository.execute(dataModelDTO);

        assertThat(insertCount).isEqualTo(1);

        Map<String, Object> result = jdbcTemplate.queryForObject("SELECT * FROM sensor_test WHERE sensor_id = 123456",
                (rs, rowNum) -> Map.of("sensor_id", rs.getString("sensor_id"),
                        "latitude", rs.getString("latitude"),
                        "longitude", rs.getString("longitude"),
                        "generated_at_time", rs.getObject("generated_at_time", OffsetDateTime.class))
        );
        assertThat(result).isNotNull();
        assertThat(result.get("sensor_id")).isEqualTo("123456");
        assertThat(result.get("latitude")).isEqualTo("50.987654");
        assertThat(result.get("longitude")).isEqualTo("4.123456");
        OffsetDateTime offsetDateTimeResult = (OffsetDateTime) result.get("generated_at_time");
        assertThat(offsetDateTimeResult).isEqualTo(offsetDateTime);
    }

    @Test
    @Sql("/db/sensor-schema.sql")
    @Sql("/db/sensor-unique-constraint.sql")
    @Sql("/db/sensor-values.sql")
    void given_uniqueConstraintAndOneSensorInDatabase_when_insertSameSensor_then_noDuplicates() {
        DbRepository dbRepository = new DbRepository(jdbcTemplate, statementCreationService, "sensor_test", true);
        List<String> columns = List.of("sensor_id", "latitude", "longitude", "generated_at_time");
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2024-12-18T13:00:40.575Z");
        List<Object> values = List.of("123456", "50.987654", "4.123456", offsetDateTime);
        DataModelDTO dataModelDTO = new DataModelDTO(new ColumnsDTO(columns));
        dataModelDTO.setData(new ValuesDTO(List.of(values)));
        int insertCount = dbRepository.execute(dataModelDTO);

        assertThat(insertCount).isEqualTo(0);

        var result = jdbcTemplate.query("SELECT * FROM sensor_test WHERE sensor_id = 123456",
                (rs, rowNum) -> Map.of("sensor_id", rs.getString("sensor_id"),
                        "latitude", rs.getString("latitude"),
                        "longitude", rs.getString("longitude"),
                        "generated_at_time", rs.getObject("generated_at_time", OffsetDateTime.class))
        );
        assertThat(result.size()).isEqualTo(1);
        for(var row : result) {
            assertThat(row.get("sensor_id")).isEqualTo("123456");
            assertThat(row.get("latitude")).isEqualTo("50.987654");
            assertThat(row.get("longitude")).isEqualTo("4.123456");
            OffsetDateTime offsetDateTimeResult = (OffsetDateTime) row.get("generated_at_time");
            assertThat(offsetDateTimeResult).isEqualTo(offsetDateTime);
        }
    }

    @Test
    @Sql("/db/hindrance-schema.sql")
    void given_emptyDatabase_when_insertHindrance_then_hindranceGetsInserted() {
        DbRepository dbRepository = new DbRepository(jdbcTemplate, statementCreationService, "hindrance", true);
        List<String> columns = List.of("gipod_id", "adms_identifier", "description", "zone", "modified");
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2021-01-06T17:11:43.167Z");
        List<Object> values = List.of(Integer.valueOf("10590330"), "_:b9bd9a05c19b65b1b60d807ae45fa4e7", "Verhuiswagen",
                "https://gipod.api.vlaanderen.be/api/v1/mobility-hindrances/10590330/zones/019e77f3-e2fd-4624-b6d7-8cfdbe57683f",
                offsetDateTime);
        DataModelDTO dataModelDTO = new DataModelDTO(new ColumnsDTO(columns));
        dataModelDTO.setData(new ValuesDTO(List.of(values)));
        int insertCount = dbRepository.execute(dataModelDTO);

        assertThat(insertCount).isEqualTo(1);

        Map<String, Object> result = jdbcTemplate.queryForObject("SELECT * FROM hindrance WHERE gipod_id = 10590330",
                (rs, rowNum) -> Map.of("gipod_id", rs.getInt("gipod_id"),
                        "adms_identifier", rs.getString("adms_identifier"),
                        "description", rs.getString("description"),
                        "zone", rs.getString("zone"),
                        "modified", rs.getObject("modified", OffsetDateTime.class))
        );
        assertThat(result).isNotNull();
        assertThat(result.get("gipod_id")).isEqualTo(Integer.valueOf("10590330"));
        assertThat(result.get("adms_identifier")).isEqualTo("_:b9bd9a05c19b65b1b60d807ae45fa4e7");
        assertThat(result.get("description")).isEqualTo("Verhuiswagen");
        assertThat(result.get("zone")).isEqualTo("https://gipod.api.vlaanderen.be/api/v1/mobility-hindrances/10590330/zones/019e77f3-e2fd-4624-b6d7-8cfdbe57683f");
        assertThat(result.get("modified")).isEqualTo(offsetDateTime);
    }


    @TestConfiguration
    static class TestConfig {
        @Bean
        StatementCreationService statementCreationService() {
            return new StatementCreationService();
        }
    }
}