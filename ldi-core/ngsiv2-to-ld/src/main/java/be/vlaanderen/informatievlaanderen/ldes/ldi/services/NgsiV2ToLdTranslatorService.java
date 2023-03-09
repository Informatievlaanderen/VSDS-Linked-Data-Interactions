package be.vlaanderen.informatievlaanderen.ldes.ldi.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.InvalidNgsiLdContextException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valuobjects.LinkedDataModel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NgsiV2ToLdTranslatorService {

	private final String coreContext;
	private final String ldContext;

	public NgsiV2ToLdTranslatorService(String coreContext) {
		this(coreContext, null);
	}

	public NgsiV2ToLdTranslatorService(String coreContext, String ldContext) {
		this.coreContext = coreContext;
		this.ldContext = ldContext;
	}

	public LinkedDataModel translate(String data) {
		return translate(data, ldContext);
	}

	public LinkedDataModel translate(String data, String ldContext) {
		try {
			LinkedDataModel model = new ObjectMapper()
					.readerFor(LinkedDataModel.class)
					.readValue(data);

			if (coreContext == null) {
				throw new InvalidNgsiLdContextException("Core context can't be null");
			}
			model.addContextDeclaration(coreContext);

			if (ldContext != null) {
				model.addContextDeclaration(ldContext);
			}

			return model;
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}
