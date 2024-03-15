package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.ParseToJsonException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.UnsupportedMimeTypeException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.parser.JenaContextProvider;
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
	private static final String CONTEXT = "@context";
	private final String context;
	private final boolean forceContentType;
	private final int maxCacheCapacity;

	public JsonToLdAdapter(String context, boolean forceContentType, int maxCacheCapacity) {
		if (context == null) {
			throw new IllegalArgumentException("Core context can't be null");
		}
		this.context = context;
		this.forceContentType = forceContentType;
		this.maxCacheCapacity = maxCacheCapacity;
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

	private Stream<Model> translateJsonToLD(String data) {
		try {
			final var json = JSON.parseAny(data);
			if (json.isObject()) {
				return Stream.of(mapJsonObjectToModel(json));
			}

			if (json.isArray()) {
				final JsonArray jsonArray = json.getAsArray();
				return jsonArray.stream().map(this::mapJsonObjectToModel);
			}

			throw new IllegalArgumentException("Only objects and arrays can be transformed to RDF. " +
					"The following json does not match this criteria: " + json);
		} catch (JsonParseException e) {
			throw new ParseToJsonException(e, data);
		}
	}

	private Model mapJsonObjectToModel(JsonValue json) {
		if (json.isObject()) {
			final var jsonObject = json.getAsObject();
			addContexts(jsonObject);
			Model model = ModelFactory.createDefaultModel();
			RDFParser.fromString(jsonObject.toString())
					.lang(Lang.JSONLD)
					.context(JenaContextProvider.create()
							.withMaxJsonLdCacheCapacity(maxCacheCapacity)
							.getContext())
					.parse(model);
			return model;
		} else {
			throw new IllegalArgumentException("Only objects can be transformed to RDF. " +
					"The following json does not match this criteria: " + json);
		}
	}

	private void addContexts(JsonObject json) {
		try {
			var contextObject = JSON.parse(context);
			if (contextObject.isObject()) {
				if (!contextObject.hasKey(CONTEXT)) {
					throw new IllegalArgumentException("Received JSON-LD context object without @context entry");
				}
				json.put(CONTEXT, contextObject.get(CONTEXT));
			}
		} catch (JsonParseException e) {
			json.put(CONTEXT, context);
		}
	}

}
