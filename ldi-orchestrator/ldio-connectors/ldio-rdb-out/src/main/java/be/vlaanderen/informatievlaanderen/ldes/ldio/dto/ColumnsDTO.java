package be.vlaanderen.informatievlaanderen.ldes.ldio.dto;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public record ColumnsDTO(List<String> columns) {

    @Override
    public String toString() {
        if (columns() == null) return null;
        return "ColumnsDTO{" +
                "columns=" +
                StringUtils.join(columns(), ", ") +
                "}";
    }
}
