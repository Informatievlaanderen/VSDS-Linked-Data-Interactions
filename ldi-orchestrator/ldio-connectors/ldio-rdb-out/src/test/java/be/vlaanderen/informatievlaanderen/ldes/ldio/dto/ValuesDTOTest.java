package be.vlaanderen.informatievlaanderen.ldes.ldio.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValuesDTOTest {

    @Test
    void given_null_when_toString_then_returnNull() {
        assertThat(new ValuesDTO(null).toString()).isNull();
    }

    @Test
    void given_emptyList_when_toString_then_returnEmptyList() {
        assertThat(new ValuesDTO(List.of()).toString()).isEqualTo("[]");
    }

    @Test
    void given_listWithOneElement_when_toString_then_returnListWithOneElement() {
        assertThat(new ValuesDTO(List.of(List.of("value"))).toString()).isEqualTo("ValuesDTO{values=value}");
    }

    @Test
    void given_listWithMultipleElements_when_toString_then_returnListWithMultipleElements() {
        assertThat(new ValuesDTO(List.of(List.of("value1"), List.of("value2"))).toString()).isEqualTo("ValuesDTO{values={value1}, {value2}}");
    }

    @Test
    void given_listWithMultipleMultipleElements_when_toString_then_returnListWithMultipleElements() {
        assertThat(new ValuesDTO(List.of(List.of("value1a", "value1b"), List.of("value2a", "value2b"))).toString())
                .isEqualTo("ValuesDTO{values={value1a, value1b}, {value2a, value2b}}");
    }
}