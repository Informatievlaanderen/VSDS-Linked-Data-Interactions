package ldes.client.performance;

public enum FragmentSize {

	// @formatter:off
	TEN("http://localhost:10101/mobility-hindrances/pagination10?pageNumber=1"),
	HUNDRED("http://localhost:10101/mobility-hindrances/pagination100?pageNumber=1"),
	TWOFIFTY("http://localhost:10101/mobility-hindrances/pagination250?pageNumber=1"),
	EXT("http://localhost:8080/mobility-hindrances/pagination250?pageNumber=1"),
	EXT_TWOFIFTY("http://localhost:8080/observations/pagination250?pageNumber=1"),
	EXT_FIVE_HUNDRED("http://localhost:8080/observations/pagination500?pageNumber=1"),
	EXT_THOUSAND("http://localhost:8080/observations/pagination1000?pageNumber=1");
	// @formatter:on

	private final String startingEndpoint;

	FragmentSize(String startingEndpoint) {
		this.startingEndpoint = startingEndpoint;
	}

	public String getStartingEndpoint() {
		return startingEndpoint;
	}

}
