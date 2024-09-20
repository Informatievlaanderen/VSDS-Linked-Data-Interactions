package ldes.client.performance;

import org.apache.jena.riot.Lang;

public enum TestScenario {

	// @formatter:off
	S_10(FragmentSize.TEN),
	S_250(FragmentSize.TWOFIFTY),
	EXTERNAL(FragmentSize.EXT),
	EXTERNAL_250_TURTLE(FragmentSize.EXT_TWOFIFTY, Lang.TURTLE),
	EXTERNAL_1000_TURTLE(FragmentSize.EXT_THOUSAND, Lang.TURTLE),
	EXTERNAL_500_TURTLE(FragmentSize.EXT_TWOFIFTY, Lang.TURTLE),
	EXTERNAL_250_PROTOBUF(FragmentSize.EXT_TWOFIFTY, Lang.RDFPROTO),
	EXTERNAL_500_PROTOBUF(FragmentSize.EXT_TWOFIFTY, Lang.RDFPROTO),
	EXTERNAL_1000_PROTOBUF(FragmentSize.EXT_THOUSAND, Lang.RDFPROTO);
	// @formatter:on

	private final FragmentSize fragmentSize;
	private final Lang sourceFormat;

	TestScenario(FragmentSize fragmentSize) {
		this(fragmentSize, Lang.TURTLE);
	}

	TestScenario(FragmentSize fragmentSize, Lang sourceFormat) {
		this.fragmentSize = fragmentSize;
		this.sourceFormat = sourceFormat;
	}

	public String getStartingEndpoint() {
		return fragmentSize.getStartingEndpoint();
	}

	public Lang getSourceFormat() {
		return sourceFormat;
	}
}
