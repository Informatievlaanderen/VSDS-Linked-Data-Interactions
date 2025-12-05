package be.vlaanderen.informatievlaanderen.ldes.ldio.dto;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public record ValuesDTO(List<List<Object>> values) {
    @Override
    public String toString() {
        if (values() == null) return null;
        if (values().isEmpty()) return "[]";

        StringBuilder sb = new StringBuilder("ValuesDTO{values=");
        values().stream()
                .map(value -> ((value != null) ? StringUtils.join(value, ", ") : "null"))
                .reduce((a, b) -> "{" + a + "}, {" + b + "}")
                .ifPresent(sb::append);
        return sb.append("}").toString();
    }
}
