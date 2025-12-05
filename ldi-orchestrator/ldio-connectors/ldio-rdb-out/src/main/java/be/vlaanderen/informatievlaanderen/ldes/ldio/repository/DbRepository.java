package be.vlaanderen.informatievlaanderen.ldes.ldio.repository;


import be.vlaanderen.informatievlaanderen.ldes.ldio.dto.DataModelDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class DbRepository {
    private final Log logger = LogFactory.getLog(getClass());
    private final JdbcTemplate jdbcTemplate;
    private final String tableName;
    private final Boolean ignoreDuplicateKeyException;
    private final StatementCreationService statementCreationService;

    private String insertStatement;

    public DbRepository(JdbcTemplate jdbcTemplate, StatementCreationService statementCreationService, String tableName, Boolean ignoreDuplicateKeyException) {
        if (jdbcTemplate == null || statementCreationService == null || tableName == null) {
            throw new IllegalArgumentException("Argument must not be null");
        }
        this.jdbcTemplate = jdbcTemplate;
        this.statementCreationService = statementCreationService;
        this.tableName = tableName;
        this.ignoreDuplicateKeyException = ignoreDuplicateKeyException;
    }

    public int execute(DataModelDTO dataModelDTO) {
        if (dataModelDTO == null ||
                dataModelDTO.getColumns() == null || dataModelDTO.getColumns().columns().isEmpty()) {
            throw new IllegalArgumentException("Invalid data model or no valid columns in the data model");
        }
        if (dataModelDTO.getData() == null || dataModelDTO.getData().values().isEmpty()) {
            return 0;
        }

        int insertCount;
        if (insertStatement == null) {
            insertStatement = statementCreationService.createInsertStatement(tableName, dataModelDTO.getColumns().columns());
        }
        if (dataModelDTO.getData().values().size() == 1) {
            try {
                insertCount = jdbcTemplate.update(insertStatement, dataModelDTO.getData().values().getFirst().toArray());
            } catch (DuplicateKeyException duplicateKeyException) {
                if (ignoreDuplicateKeyException) {
                    insertCount = 0;
                    logger.warn("Duplicate key found: " + duplicateKeyException.getMessage());
                } else {
                    throw duplicateKeyException;
                }
            }
        } else {
            List<List<Object>> values = dataModelDTO.getData().values();
            List<Object[]> listOfObjects = values.stream().map(List::toArray).toList();
            int[] insertCounts = jdbcTemplate.batchUpdate(insertStatement, listOfObjects);
            insertCount = insertCounts.length;
        }
        return insertCount;
    }
}
