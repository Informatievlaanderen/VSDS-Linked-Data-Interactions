package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class JsonConverter implements AttributeConverter<Object, String> {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(Object attribute) {
		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Error converting to JSON", e);
		}
	}

	@Override
	public Object convertToEntityAttribute(String dbData) {
		try {
			return objectMapper.readValue(dbData, Object.class);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Error converting from JSON", e);
		}
	}
}
