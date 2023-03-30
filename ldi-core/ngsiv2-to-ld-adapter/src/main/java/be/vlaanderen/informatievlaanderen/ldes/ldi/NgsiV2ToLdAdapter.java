package be.vlaanderen.informatievlaanderen.ldes.ldi;

import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.rdf.model.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.DeserializationFromJsonException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.InvalidNgsiLdContextException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.UnsupportedMimeTypeException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.LinkedDataModel;

public class NgsiV2ToLdAdapter implements LdiAdapter {

	private final String coreContext;
	private final String ldContext;
	private final String dataIdentifier;

	public NgsiV2ToLdAdapter(String dataIdentifier, String coreContext) {
		this(dataIdentifier, coreContext, null);
	}

	public NgsiV2ToLdAdapter(String dataIdentifier, String coreContext, String ldContext) {
		if (dataIdentifier == null) {
			throw new InvalidNgsiLdContextException("Can't identify data with the data array key");
		}

		if (coreContext == null) {
			throw new InvalidNgsiLdContextException("Core context can't be null");
		}
		this.dataIdentifier = dataIdentifier;
		this.coreContext = coreContext;
		this.ldContext = ldContext;
	}

	public Stream<LinkedDataModel> translateJsonToLD(String data) {
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
			return Arrays.stream(models)
					.map(model -> {
						addContexts(model);
						return model;
					});
		} catch (JsonProcessingException e) {
			throw new DeserializationFromJsonException(e, data);
		}
	}

	private void addContexts(LinkedDataModel model) {
		model.addContextDeclaration(coreContext);
		if (ldContext != null) {
			model.addContextDeclaration(ldContext);
		}
	}

	public Stream<Model> translate(String data) {
		JsonObject parsedData = JSON.parse(data);
		String value = String.valueOf(parsedData.get(dataIdentifier));
		return translateJsonToLD(value).map(LinkedDataModel::toRDFModel);
	}

	@Override
	public Stream<Model> apply(Content content) {
		if (!content.mimeType().equalsIgnoreCase("application/json")) {
			throw new UnsupportedMimeTypeException("application/json", content.mimeType());
		}
		return translate(content.content());
	}
}
