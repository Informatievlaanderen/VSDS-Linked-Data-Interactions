package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.ParseToJsonException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.UnsupportedMimeTypeException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.apache.http.entity.ContentType;
import org.apache.jena.atlas.json.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class JsonToLdAdapter implements LdiAdapter {

	private final Logger log = LoggerFactory.getLogger(JsonToLdAdapter.class);

	private static final String MIMETYPE = "application/json";
	private final String coreContext;
	private final boolean forceContentType;

	public JsonToLdAdapter(String coreContext) {
		this(coreContext, false);
	}

	public JsonToLdAdapter(String coreContext, boolean forceContentType) {
		if (coreContext == null) {
			throw new IllegalArgumentException("Core context can't be null");
		}
		this.coreContext = coreContext;
		this.forceContentType = forceContentType;
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

	// TODO TVB: 28/12/23 test array flow
	// TODO TVB: 28/12/23 test exception flow
	private Stream<Model> translateJsonToLD(String data) {
		try {
			final var json = JSON.parseAny(data);
			if (json.isObject()) {
				return Stream.of(translateJsonToLD(json));
			}

			if (json.isArray()) {
				final JsonArray jsonArray = json.getAsArray();
				return jsonArray.stream().map(this::translateJsonToLD);
			}

			throw new IllegalArgumentException("Only objects and arrays can be transformed to RDF");
		} catch (JsonParseException e) {
			throw new ParseToJsonException(e, data);
		}
	}

	private Model translateJsonToLD(JsonValue json) {
		if (json.isObject()) {
			final var jsonObject = json.getAsObject();
			addContexts(jsonObject);
			Model model = ModelFactory.createDefaultModel();
			RDFParser.fromString(jsonObject.toString())
					.lang(Lang.JSONLD)
					.parse(model);
			return model;
		} else {
			throw new IllegalArgumentException("Only objects can be transformed to RDF");
		}
	}

	private void addContexts(JsonObject json) {
		JsonArray contexts = new JsonArray();
		contexts.add(coreContext);
		json.put("@context", contexts);
	}

}
