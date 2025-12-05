package be.vlaanderen.informatievlaanderen.ldes.ldio.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DataModelDTOTest {

    @Test
    void given_listOfColumnWithOneTestValue_when_toString_then_returnColumnAndData() {
        List<List<Object>> testValue = List.of(List.of("testValue"));
        DataModelDTO dataModelDTO = new DataModelDTO(new ColumnsDTO(List.of("test")));
        dataModelDTO.setData(new ValuesDTO(testValue));
        assertThat(dataModelDTO.toString()).isEqualTo("DataModelDTO{columns=ColumnsDTO{columns=test}, data=ValuesDTO{values=testValue}}");
    }
}