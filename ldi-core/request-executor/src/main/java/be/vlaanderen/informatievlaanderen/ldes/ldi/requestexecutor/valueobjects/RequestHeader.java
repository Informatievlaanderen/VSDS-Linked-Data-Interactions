package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

public class RequestHeader {

	private final String key;
	private final String value;

	public RequestHeader(String key, String value) {
		this.key = notNull(key, "HeaderKey cannot be null");
		this.value = notNull(value);
	}

	public static RequestHeader from(String headerString) {
		int indexFirstColon = headerString.indexOf(":");
		String key = headerString.substring(0, indexFirstColon).trim();
		String value = headerString.substring(indexFirstColon + 1).trim();
		return new RequestHeader(key, value);
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		RequestHeader that = (RequestHeader) o;
		return Objects.equals(key, that.key) && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}

}
