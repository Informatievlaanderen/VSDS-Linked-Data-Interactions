package be.vlaanderen.informatievlaanderen.ldes.ldi.services;


import be.vlaanderen.informatievlaanderen.ldes.ldi.NgsiV2ToLdTranslatorDefaults;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class NgsiLdURIParser {

	private NgsiLdURIParser() {
	}

	public static String toNgsiLdUri(String entityId, String entityType) {
		URI uri;
		try {
			uri = new URI(entityId);
		} catch (URISyntaxException e) {
			uri = null;
		}

		if (uri == null || uri.getScheme() == null
				|| !Arrays.asList(NgsiV2ToLdTranslatorDefaults.getAllowedSchemes()).contains(uri.getScheme())) {
			return NgsiV2ToLdTranslatorDefaults.NGSI_LD_URI_PREFIX + ":" + entityType + ":" + entityId;
		}

		return entityId;
	}

	public static String toNgsiLdObjectUri(String entityId, String value) {
		String entityType = "";
		if (entityId.toLowerCase().startsWith(NgsiV2ToLdTranslatorDefaults.NGSI_LD_REFERENCE_PREFIX)) {
			entityType = entityId.substring(3);
		}

		return toNgsiLdUri(value, entityType);
	}
}
