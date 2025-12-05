package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioRdbOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.converter.ValueConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioOutputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.repository.DbRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldio.repository.StatementCreationService;
import be.vlaanderen.informatievlaanderen.ldes.ldio.sparqlselect.SparqlSelectService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioRdbOut.NAME;

@Configuration
public class LdioRdbOutAutoConfig {
    private final JdbcTemplate jdbcTemplate;
    private final Log logger = LogFactory.getLog(getClass());

    public LdioRdbOutAutoConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings("java:S6830")
    @Bean(NAME)
    public LdioOutputConfigurator ldioConfigurator() {
        logger.debug("Creating Ldio Configurator");
        return new LdioRdbOutConfigurator(jdbcTemplate);
    }

    public static class LdioRdbOutConfigurator implements LdioOutputConfigurator {
        public static final String PROPERTY_TABLE_NAME = "table-name";
        public static final String PROPERTY_SPARQL_SELECT_QUERY = "sparql-select-query";
        public static final String PROPERTY_IGNORE_DUPLICATE_KEY_EXCEPTION = "ignore-duplicate-key-exception";
        private final Log logger = LogFactory.getLog(getClass());
        private final JdbcTemplate jdbcTemplate;

        public LdioRdbOutConfigurator(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        @Override
        public LdiComponent configure(ComponentProperties properties) {
            if (properties.getProperty(PROPERTY_TABLE_NAME) == null || properties.getProperty(PROPERTY_SPARQL_SELECT_QUERY) == null) {
                throw new IllegalArgumentException("The configuration for '%s' and '%s' is missing".formatted(PROPERTY_TABLE_NAME, PROPERTY_SPARQL_SELECT_QUERY));
            }

            // SPARQL Select Service
            ValueConverter valueConverter = new ValueConverter();
            SparqlSelectService sparqlSelectService = new SparqlSelectService(valueConverter);
            // DB Repository
            String tableName = properties.getProperty(PROPERTY_TABLE_NAME);
            String sparqlSelectQuery = properties.getProperty(PROPERTY_SPARQL_SELECT_QUERY);
            Boolean ignoreDuplicateKeyException = properties.getOptionalBoolean(PROPERTY_IGNORE_DUPLICATE_KEY_EXCEPTION).orElse(false);
            StatementCreationService statementCreationService = new StatementCreationService();
            DbRepository dbRepository = new DbRepository(jdbcTemplate, statementCreationService, tableName, ignoreDuplicateKeyException);
            LdioRdbOut ldioRdbOut = new LdioRdbOut(properties, dbRepository, sparqlSelectService, tableName, sparqlSelectQuery, ignoreDuplicateKeyException);
            logger.debug("Created LdioRdbOut: " + ldioRdbOut);
            return ldioRdbOut;
        }
    }
}
