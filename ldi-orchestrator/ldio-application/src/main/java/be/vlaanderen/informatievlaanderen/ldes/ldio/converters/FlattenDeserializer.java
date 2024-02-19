package be.vlaanderen.informatievlaanderen.ldes.ldio.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FlattenDeserializer extends JsonDeserializer<Map<String, String>> {
	@Override
	public Map<String, String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		Map<String, String> result = new HashMap<>();
		JsonNode node = p.getCodec().readTree(p);
		flatten("", node, result);
		return result;
	}

	private void flatten(String prefix, JsonNode node, Map<String, String> result) {
		if (node.isObject()) {
			Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
			while (fields.hasNext()) {
				Map.Entry<String, JsonNode> field = fields.next();
				String fieldName = prefix.isEmpty() ? field.getKey() : prefix + "." + field.getKey();
				flatten(fieldName, field.getValue(), result);
			}
		} else if (node.isValueNode()) {
			result.put(prefix, node.asText());
		}
	}
}
