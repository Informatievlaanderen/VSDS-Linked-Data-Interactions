package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.web;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A custom deserializer that flattens a JSON object into a Map of Strings.
 * The keys in the map are the JSON paths of the values in the input JSON.
 * This deserializer extends the JsonDeserializer class from the Jackson library.
 */
public class FlattenDeserializer extends JsonDeserializer<Map<String, String>> {
	/**
	 * Deserializes a JSON object into a flattened map.
	 *
	 * @param parser  The JSON parser.
	 * @param context The deserialization context.
	 * @return A map where the keys are JSON paths and the values are the corresponding values from the input JSON.
	 * @throws IOException If an input or output exception occurred.
	 */
	@Override
	public Map<String, String> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		Map<String, String> result = new HashMap<>();
		JsonNode node = parser.getCodec().readTree(parser);
		flatten("", node, result);
		return result;
	}

	/**
	 * Recursively flattens a JSON node.
	 *
	 * @param prefix The current path prefix.
	 * @param node   The current JSON node.
	 * @param result The result map.
	 */
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
		} else if (node.isArray()) {
			for (int i = 0; i < node.size(); i++) {
				flatten(prefix + "." + i, node.get(i), result);
			}
		}
	}
}
