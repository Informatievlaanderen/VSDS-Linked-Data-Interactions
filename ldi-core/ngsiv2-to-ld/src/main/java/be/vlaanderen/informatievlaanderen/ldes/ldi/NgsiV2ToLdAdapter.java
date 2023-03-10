package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.InvalidNgsiLdContextException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.LinkedDataModel;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.rdf.model.Model;

import java.util.Arrays;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NgsiV2ToLdAdapter implements LdiAdapter {

	private final String coreContext;
	private final String ldContext;

	public NgsiV2ToLdAdapter(String coreContext) {
		this(coreContext, null);
	}

	public NgsiV2ToLdAdapter(String coreContext, String ldContext) {
		if (coreContext == null) {
			throw new InvalidNgsiLdContextException("Core context can't be null");
		}
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
			throw new RuntimeException(e);
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
		String value = String.valueOf(parsedData.get("value"));
		return translateJsonToLD(value).map(LinkedDataModel::toRDFModel);
	}

	@Override
	public Stream<Model> apply(Content content) {
		if (!content.mimeType().equalsIgnoreCase("application/json")) {
			throw new RuntimeException("Requested mimetype should be application/json");
		}
		return translate(content.content());
	}
}
