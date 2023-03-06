package ldes.client.requestexecutor.domain.valueobjects;

import static org.apache.commons.lang3.Validate.notNull;

public class RequestHeader {
	
	private final String key;
	private final String value;

	public RequestHeader(String key, String value) {
		this.key = notNull(key, "HeaderKey cannot be null");
		this.value = value;
	}

	public String getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
	
}
