package be.vlaanderen.informatievlaanderen.ldes.ldio.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnsDTOTest {
    @Test
    void given_null_when_toString_then_returnNull() {
        assertThat(new ColumnsDTO(null).toString()).isNull();
    }

    @Test
    void given_oneElementList_when_toString_then_returnListWithOneElement() {
        assertThat(new ColumnsDTO(List.of("test")).toString()).isEqualTo("ColumnsDTO{columns=test}");
    }

    @Test
    void given_multipleElementList_when_toString_then_returnListWithMultipleElements() {
        assertThat(new ColumnsDTO(List.of("test1", "test2")).toString()).isEqualTo("ColumnsDTO{columns=test1, test2}");
    }
}