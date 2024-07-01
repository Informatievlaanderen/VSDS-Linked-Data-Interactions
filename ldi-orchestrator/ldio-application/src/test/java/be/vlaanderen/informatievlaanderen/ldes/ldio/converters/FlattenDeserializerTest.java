package be.vlaanderen.informatievlaanderen.ldes.ldio.converters;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.dto.deserialisers.FlattenDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlattenDeserializerTest {

	private ObjectMapper mapper;
	private FlattenDeserializer deserializer;

	@BeforeEach
	public void setup() {
		mapper = new ObjectMapper();
		deserializer = new FlattenDeserializer();
	}

	@Test
	void floating_point_string_deserialises_to_Double_value() throws IOException {
		String json = """
				{
				   "config": {
				     "key": "val",
				     "level1": {
				       "level2": {
				         "key": "val",
				         "level3": {
				           "key": "val",
				           "list": [
				           	 "list1-val",
				           	 "list2-val"
				           ]
				         }
				       }
				     }
				   }
				 }
				""";
		Map<String, String> flattenendMap = flattenMap(json);
		assertEquals(5, flattenendMap.size());
		assertTrue(flattenendMap.containsKey("key"));
		assertEquals("val", flattenendMap.get("key"));
		assertTrue(flattenendMap.containsKey("level1.level2.key"));
		assertEquals("val", flattenendMap.get("level1.level2.key"));
		assertTrue(flattenendMap.containsKey("level1.level2.level3.key"));
		assertEquals("val", flattenendMap.get("level1.level2.level3.key"));
		assertEquals("list1-val", flattenendMap.get("level1.level2.level3.list.0"));
		assertEquals("list2-val", flattenendMap.get("level1.level2.level3.list.1"));
	}

	private Map<String, String> flattenMap(String json) throws IOException {
		InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
		JsonParser parser = mapper.getFactory().createParser(stream);
		DeserializationContext ctxt = mapper.getDeserializationContext();
		parser.nextToken();
		parser.nextToken();
		parser.nextToken();
		return deserializer.deserialize(parser, ctxt);
	}
}
