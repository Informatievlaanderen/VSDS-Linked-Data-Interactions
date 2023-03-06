package ldes.client.requestexecutor.domain.valueobjects;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

public class RequestHeader {
	
	private final String key;
	private final String value;

	public RequestHeader(String key, String value) {
		this.key = notNull(key, "HeaderKey cannot be null");
		this.value = notNull(value);
	}

	public String getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RequestHeader that = (RequestHeader) o;
		return key.equals(that.key) && value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}
}
