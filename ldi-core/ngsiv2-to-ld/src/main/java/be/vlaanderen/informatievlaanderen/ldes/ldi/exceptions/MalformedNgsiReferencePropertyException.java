package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.config.NgsiV2ToLdMapping.NGSI_LD_ATTRIBUTE_TYPE_RELATIONSHIP;

public class MalformedNgsiReferencePropertyException extends RuntimeException {
	private final String className;
	private final String value;

	public MalformedNgsiReferencePropertyException(String className, String value) {
		super();
		this.className = className;
		this.value = value;
	}

	@Override
	public String getMessage() {
		return "property of type " + NGSI_LD_ATTRIBUTE_TYPE_RELATIONSHIP + "must contain a value of type String. "
				+ "Found object with type: " + className + " and value: " + value;
	}
}
