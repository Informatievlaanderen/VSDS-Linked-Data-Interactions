package ldes.client.requestexecutor.domain.valueobjects;

public class RequestHeader {
	private final String key;
	private final String value;

	public RequestHeader(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
