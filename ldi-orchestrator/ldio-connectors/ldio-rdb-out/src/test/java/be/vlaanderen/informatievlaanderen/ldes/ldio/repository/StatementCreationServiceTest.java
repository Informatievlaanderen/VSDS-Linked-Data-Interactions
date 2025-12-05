package be.vlaanderen.informatievlaanderen.ldes.ldio.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class StatementCreationServiceTest {
    private final StatementCreationService statementCreationService = new StatementCreationService();
    private final String tableName = "tableName";


    public static Stream<Arguments> sqlArguments() {
        return Stream.of(
                Arguments.of(List.of("id", "name", "age"), "?, ?, ?")
        );
    }

    @Test
    void when_createParametersStringWithEmptyList_then_illegalArgumentExceptionIsThrown() {
        assertThatThrownBy(() -> statementCreationService.createParametersString(List.of())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void when_createParametersStringWithNullList_then_illegalArgumentExceptionIsThrown() {
        assertThatThrownBy(() -> statementCreationService.createParametersString(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void when_createInsertStatementWithEmptyColumns_then_illegalArgumentExceptionIsThrown() {
        assertThatThrownBy(() -> statementCreationService.createInsertStatement(tableName, List.of())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void when_createInsertStatementWithNullColumns_then_illegalArgumentExceptionIsThrown() {
        assertThatThrownBy(() -> statementCreationService.createInsertStatement(tableName, null)).isInstanceOf(IllegalArgumentException.class);
    }
}