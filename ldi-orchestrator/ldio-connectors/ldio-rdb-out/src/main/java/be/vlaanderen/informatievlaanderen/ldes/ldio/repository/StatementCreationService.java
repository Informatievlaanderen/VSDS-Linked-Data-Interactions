package be.vlaanderen.informatievlaanderen.ldes.ldio.repository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatementCreationService {

    public String createInsertStatement(String tableName, List<String> columns) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("columns cannot be null or empty");
        }
        return String.format("INSERT INTO %s (%s) VALUES (%s)",
                tableName,
                String.join(", ", columns),
                createParametersString(columns)
        );
    }

    String createParametersString(List<String> columns) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("columns cannot be null or empty");
        }
        return columns.stream().map(column -> "?").reduce((a, b) -> a + ", " + b).get();
    }
}
