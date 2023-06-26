package be.vlaanderen.informatievlaanderen.ldes.ldio.conversionstrategy.json;

import be.vlaanderen.informatievlaanderen.ldes.ldio.util.MemberIdExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.util.ModelConverter;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Model2JsonConverter {

	public static final String JSON_CONTEXT_KEY = "@context";
	public static final String JSON_GRAPH_KEY = "@graph";
	public static final String JSON_ID_KEY = "id";
	public static final String JSON_AT_ID_KEY = "@id";
	public static final String NO_EXISTING_ID = "No Existing Id";
	private final MemberIdExtractor memberIdExtractor = new MemberIdExtractor();
	private final String jsonContextURI;

	public Model2JsonConverter(String jsonContextURI) {
		this.jsonContextURI = jsonContextURI;
	}

	private Map<String, Object> getOptions(String jsonContext) {
		Map<String, Object> context = new HashMap<>();
		context.put(JSON_CONTEXT_KEY, jsonContext);
		return context;
	}

	public String modelToJSONLD(Model model) throws IOException {
		String jsonSerialization = convertModelToJson(model);
		String memberId = memberIdExtractor.extractMemberId(model);
		Map<String, Object> framedJson = getFramedJson(createJsonObject(jsonSerialization), getOptions(jsonContextURI));
		return filterMember(framedJson, memberId);

	}

	private String filterMember(Map<String, Object> framedJson, String member) throws IOException {
		List<Map<String, Object>> graph = (List<Map<String, Object>>) framedJson.get(JSON_GRAPH_KEY);
		AtomicReference<String> json = new AtomicReference<>(JsonUtils.toPrettyString(graph));
		for (Map<String, Object> section : graph) {
			String sectionId = extractSectionId(section);
			if (sectionId.equals(member)) {
				section.put(JSON_CONTEXT_KEY, jsonContextURI);
				json.set(JsonUtils.toPrettyString(section));
			}
		}
		return json.get();
	}

	private String extractSectionId(Map<String, Object> section) {
		return (String) section.getOrDefault(JSON_ID_KEY, section.getOrDefault(JSON_AT_ID_KEY, NO_EXISTING_ID));
	}

	private Map<String, Object> getFramedJson(Object json, Map<String, Object> frame) {
		return JsonLdProcessor.frame(json, frame, new JsonLdOptions());
	}

	private Object createJsonObject(String ld) throws IOException {
		InputStream inputStream = new ByteArrayInputStream(ld.getBytes(StandardCharsets.UTF_8));
		return JsonUtils.fromInputStream(inputStream);
	}

	private String convertModelToJson(Model model) {
		return ModelConverter.toString(model, Lang.JSONLD11);
	}
}
