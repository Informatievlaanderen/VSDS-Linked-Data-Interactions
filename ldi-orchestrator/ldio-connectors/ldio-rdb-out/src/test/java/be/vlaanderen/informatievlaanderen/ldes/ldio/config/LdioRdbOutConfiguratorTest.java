package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioRdbOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioOutputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioRdbOutAutoConfig.LdioRdbOutConfigurator.PROPERTY_SPARQL_SELECT_QUERY;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioRdbOutAutoConfig.LdioRdbOutConfigurator.PROPERTY_TABLE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LdioRdbOutConfiguratorTest {

    public static final String TEST_TABLE = "test_table";
    public static final String DEFAULT_SPARQL_SELECT_QUERY = "SELECT * WHERE {?s ?p ?o}";

    @Test
    void given_jdbcTemplate_when_createLdioRdbOutAutoConfig_then_ldioConfiguratorBeanIsCreated() {
        JdbcTemplate jdbcTemplateMock = Mockito.mock(JdbcTemplate.class);
        LdioRdbOutAutoConfig config = new LdioRdbOutAutoConfig(jdbcTemplateMock);

        assertThat(config.ldioConfigurator()).isNotNull();
    }

    @Test
    void given_jdbcTemplate_when_createLdioRdbOutAutoConfig_then_ldioConfiguratorBeanIsInstanceOfCorrectType() {
        JdbcTemplate jdbcTemplateMock = Mockito.mock(JdbcTemplate.class);
        LdioRdbOutAutoConfig config = new LdioRdbOutAutoConfig(jdbcTemplateMock);

        assertThat(config.ldioConfigurator()).isInstanceOf(LdioRdbOutAutoConfig.LdioRdbOutConfigurator.class);
    }

    @Test
    void given_jdbcTemplate_when_createLdioRdbOutAutoConfigWithProperties_then_ldioConfiguratorBeanHasUsedProperties() {
        JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        ComponentProperties properties = Mockito.mock(ComponentProperties.class);

        when(properties.getProperty(PROPERTY_TABLE_NAME)).thenReturn(TEST_TABLE);
        when(properties.getProperty(PROPERTY_SPARQL_SELECT_QUERY)).thenReturn(DEFAULT_SPARQL_SELECT_QUERY);

        LdioOutputConfigurator configurator = new LdioRdbOutAutoConfig.LdioRdbOutConfigurator(jdbcTemplate);
        LdiComponent component = configurator.configure(properties);

        assertNotNull(component);
        assertInstanceOf(LdioRdbOut.class, component);

        verify(properties, times(2)).getProperty(PROPERTY_TABLE_NAME);
        verify(properties, times(2)).getProperty(PROPERTY_SPARQL_SELECT_QUERY);
    }

    @Test
    void when_createLdioRdbOutAutoConfigWithNoTableName_then_throwsIllegalArgumentException() {
        JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        ComponentProperties properties = Mockito.mock(ComponentProperties.class);

        when(properties.getProperty(PROPERTY_TABLE_NAME)).thenReturn(null);
        when(properties.getProperty(PROPERTY_SPARQL_SELECT_QUERY)).thenReturn(DEFAULT_SPARQL_SELECT_QUERY);

        LdioOutputConfigurator configurator = new LdioRdbOutAutoConfig.LdioRdbOutConfigurator(jdbcTemplate);

        assertThrows(IllegalArgumentException.class, () -> configurator.configure(properties));

        verify(properties, times(1)).getProperty(PROPERTY_TABLE_NAME);
    }

    @Test
    void when_createLdioRdbOutAutoConfigWithNoSparqlSelectQuery_then_throwsIllegalArgumentException() {
        JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        ComponentProperties properties = Mockito.mock(ComponentProperties.class);

        when(properties.getProperty(PROPERTY_TABLE_NAME)).thenReturn(TEST_TABLE);
        when(properties.getProperty(PROPERTY_SPARQL_SELECT_QUERY)).thenReturn(null);

        LdioOutputConfigurator configurator = new LdioRdbOutAutoConfig.LdioRdbOutConfigurator(jdbcTemplate);

        assertThrows(IllegalArgumentException.class, () -> configurator.configure(properties));

        verify(properties, times(1)).getProperty(PROPERTY_TABLE_NAME);
        verify(properties, times(1)).getProperty(PROPERTY_SPARQL_SELECT_QUERY);
    }

    @Test
    void given_jdbcTemplate_when_createLdioRdbOutAutoConfigWithPropertiesAndIgnoreDuplicateKeyException_then_ldioConfiguratorBeanHasUsedProperties() {
        JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        ComponentProperties properties = Mockito.mock(ComponentProperties.class);

        when(properties.getProperty(PROPERTY_TABLE_NAME)).thenReturn(TEST_TABLE);
        when(properties.getProperty(PROPERTY_SPARQL_SELECT_QUERY)).thenReturn(DEFAULT_SPARQL_SELECT_QUERY);
        when(properties.getOptionalBoolean("ignore-duplicate-key-exception")).thenReturn(java.util.Optional.of(true));

        LdioOutputConfigurator configurator = new LdioRdbOutAutoConfig.LdioRdbOutConfigurator(jdbcTemplate);
        LdiComponent component = configurator.configure(properties);

        assertNotNull(component);
        assertInstanceOf(LdioRdbOut.class, component);
        verify(properties, times(2)).getProperty(PROPERTY_TABLE_NAME);
        verify(properties, times(2)).getProperty(PROPERTY_SPARQL_SELECT_QUERY);
        verify(properties, times(1)).getOptionalBoolean("ignore-duplicate-key-exception");
    }
}