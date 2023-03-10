package be.vlaanderen.informatievlaanderen.ldes.ldi.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.NgsiV2ToLdMapping.*;

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
				|| !Arrays.asList(NGSI_LD_ALLOWED_URI_SCHEMES).contains(uri.getScheme())) {
			return NGSI_LD_URI_PREFIX + ":" + entityType + ":" + entityId;
		}

		return entityId;
	}
}
