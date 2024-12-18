package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.DeserializationFromJsonException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.UnsupportedMimeTypeException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.LinkedDataModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.core5.http.ContentType;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.rdf.model.Model;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Adapter that will transform a NGSI V2 input into NGSI LD.
 */
public class NgsiV2ToLdAdapter implements LdiAdapter {

	private final String coreContext;
	private final String ldContext;
	private final String dataIdentifier;

	public NgsiV2ToLdAdapter(String dataIdentifier, String coreContext, String ldContext) {
		if (dataIdentifier == null) {
			throw new IllegalArgumentException("Can't identify data with the data array key");
		}

		if (coreContext == null) {
			throw new IllegalArgumentException("Core context can't be null");
		}
		this.dataIdentifier = dataIdentifier;
		this.coreContext = coreContext;
		this.ldContext = ldContext;
	}

	Stream<LinkedDataModel> translateJsonToLD(String data) {
		try {
			LinkedDataModel model = new ObjectMapper()
					.readerFor(LinkedDataModel.class)
					.readValue(data);

			addContexts(model);
			return Stream.of(model);
		} catch (JsonProcessingException ignored) {
			// ignore exception
		}
		try {
			LinkedDataModel[] models = new ObjectMapper()
					.readerFor(LinkedDataModel[].class)
					.readValue(data);
			return Arrays.stream(models).map(this::addContexts);
		} catch (JsonProcessingException e) {
			throw new DeserializationFromJsonException(e, data);
		}
	}

	private LinkedDataModel addContexts(LinkedDataModel model) {
		model.addContextDeclaration(coreContext);
		if (ldContext != null) {
			model.addContextDeclaration(ldContext);
		}
		return model;
	}

	public Stream<Model> translate(String data) {
		JsonObject parsedData = JSON.parse(data);
		String value = String.valueOf(parsedData.get(dataIdentifier));
		return translateJsonToLD(value).map(LinkedDataModel::toRDFModel);
	}

	/**
	 *
	 * @param content
	 *            contains the to be translated string and the received MIME type.
	 *
	 * @return a stream of the translated JSON objects in jena rdf models.
	 *
	 */
	@Override
	public Stream<Model> apply(Content content) {
		if (!validateMimeType(content.mimeType())) {
			throw new UnsupportedMimeTypeException(ContentType.APPLICATION_JSON.getMimeType(), content.mimeType());
		}
		return translate(content.content());
	}

	public boolean validateMimeType(String mimeType) {
		return ContentType.parse(mimeType).getMimeType().equalsIgnoreCase(ContentType.APPLICATION_JSON.getMimeType());
	}
}
