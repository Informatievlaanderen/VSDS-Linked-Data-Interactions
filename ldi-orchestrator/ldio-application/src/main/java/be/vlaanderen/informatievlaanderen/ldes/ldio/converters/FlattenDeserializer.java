package be.vlaanderen.informatievlaanderen.ldes.ldio.converters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * A custom deserializer that flattens a JSON object into a Map of Strings.
 * The keys in the map are the JSON paths of the values in the input JSON.
 * This deserializer extends the JsonDeserializer class from the Jackson library.
 */
public class FlattenDeserializer extends JsonDeserializer<Map<String, String>> {
	/**
	 * Deserializes a JSON object into a flattened map.
	 *
	 * @param p    The JSON parser.
	 * @param ctxt The deserialization context.
	 * @return A map where the keys are JSON paths and the values are the corresponding values from the input JSON.
	 * @throws IOException If an input or output exception occurred.
	 */
	@Override
	public Map<String, String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		Map<String, String> result = new HashMap<>();
		JsonNode node = p.getCodec().readTree(p);
		flatten("", node, result);
		return result;
	}

	/**
	 * Recursively flattens a JSON node.
	 *
	 * @param prefix The current path prefix.
	 * @param node The current JSON node.
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
		}
	}
}
