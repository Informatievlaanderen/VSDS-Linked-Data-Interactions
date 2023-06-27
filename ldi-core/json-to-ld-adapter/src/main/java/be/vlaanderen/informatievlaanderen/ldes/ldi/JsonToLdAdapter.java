package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.InvalidJsonLdContextException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.ParseToJsonException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.UnsupportedMimeTypeException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.apache.http.entity.ContentType;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonParseException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import java.util.stream.Stream;

public class JsonToLdAdapter implements LdiAdapter {

	private final String coreContext;
	private final String ldContext;
	private static final String MIMETYPE = "application/json";

	public JsonToLdAdapter(String coreContext) {
		this(coreContext, null);
	}

	public JsonToLdAdapter(String coreContext, String ldContext) {
		if (coreContext == null) {
			throw new InvalidJsonLdContextException("Core context can't be null");
		}
		this.coreContext = coreContext;
		this.ldContext = ldContext;
	}

	public void addContexts(JsonObject json) {
		JsonArray contexts = new JsonArray();
		contexts.add(coreContext);
		if (ldContext != null) {
			contexts.add(ldContext);
		}
		json.put("@context", contexts);
	}

	public Stream<Model> translateJsonToLD(String data) {
		try {
			JsonObject json = JSON.parse(data);
			addContexts(json);
			return Stream.of(json).map(this::toRDFModel);
		} catch (JsonParseException e) {
			throw new ParseToJsonException(e, data);
		}
	}

	public Model toRDFModel(JsonObject json) {
		Model model = ModelFactory.createDefaultModel();
		RDFParser.fromString(json.toString())
				.lang(Lang.JSONLD)
				.parse(model);
		return model;
	}

	@Override
	public Stream<Model> apply(Content content) {
		if (!validateMimeType(content.mimeType())) {
			throw new UnsupportedMimeTypeException(MIMETYPE, content.mimeType());
		}
		return translateJsonToLD(content.content());
	}

	public boolean validateMimeType(String mimeType) {
		return ContentType.parse(mimeType).getMimeType().equalsIgnoreCase(MIMETYPE);
	}

}
