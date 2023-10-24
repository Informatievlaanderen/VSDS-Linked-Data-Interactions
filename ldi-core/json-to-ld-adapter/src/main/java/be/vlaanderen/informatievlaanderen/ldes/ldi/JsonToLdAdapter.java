package be.vlaanderen.informatievlaanderen.ldes.ldi;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class JsonToLdAdapter implements LdiAdapter {

	private final Logger log = LoggerFactory.getLogger(JsonToLdAdapter.class);

	private final String coreContext;
	private final String ldContext;
	private static final String MIMETYPE = "application/json";
	private final boolean forceContentType;

	public JsonToLdAdapter(String coreContext) {
		this(coreContext, null, false);
	}

	public JsonToLdAdapter(String coreContext, String ldContext, boolean forceContentType) {
		if (coreContext == null) {
			throw new IllegalArgumentException("Core context can't be null");
		}
		this.coreContext = coreContext;
		this.ldContext = ldContext;
		this.forceContentType = forceContentType;
	}

	private void addContexts(JsonObject json) {
		JsonArray contexts = new JsonArray();
		contexts.add(coreContext);
		if (ldContext != null) {
			contexts.add(ldContext);
		}
		json.put("@context", contexts);
	}

	private Stream<Model> translateJsonToLD(String data) {
		try {
			JsonObject json = JSON.parse(data);
			addContexts(json);
			return Stream.of(json).map(this::toRDFModel);
		} catch (JsonParseException e) {
			throw new ParseToJsonException(e, data);
		}
	}

	private Model toRDFModel(JsonObject json) {
		Model model = ModelFactory.createDefaultModel();
		RDFParser.fromString(json.toString())
				.lang(Lang.JSONLD)
				.parse(model);
		return model;
	}

	@Override
	public Stream<Model> apply(Content content) {
		if (!validateMimeType(content.mimeType())) {
			if (forceContentType) {
				log.warn("Invalid mimeType {} was forced to application/json", content.mimeType());
			} else {
				throw new UnsupportedMimeTypeException(MIMETYPE, content.mimeType());
			}
		}
		return translateJsonToLD(content.content());
	}

	private boolean validateMimeType(String mimeType) {
		return ContentType.parse(mimeType).getMimeType().equalsIgnoreCase(MIMETYPE);
	}

}
