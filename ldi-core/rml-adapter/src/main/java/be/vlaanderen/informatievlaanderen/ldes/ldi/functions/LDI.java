package be.vlaanderen.informatievlaanderen.ldes.ldi.functions;

public class LDI {
	private LDI() {
	}

	static final String PREFIX = "http://www.vlaanderen.be/ns/ldi#";
	// Functions
	static final String TO_WKT_FUNCTION = PREFIX + "toWkt";
	static final String REPLACE_FUNCTION = PREFIX + "replace";
	static final String EPOCH_TO_ISO8601_FUNCTION = PREFIX + "epochToIso8601";
	// Properties
	static final String COORDINATES = PREFIX + "coordinates";
	static final String WKT_TYPE = PREFIX + "wktType";
	static final String CONTENT = PREFIX + "content";
	static final String TARGET = PREFIX + "target";
	static final String REPLACEMENT = PREFIX + "replacement";
	static final String EPOCH = PREFIX + "epoch";
}
