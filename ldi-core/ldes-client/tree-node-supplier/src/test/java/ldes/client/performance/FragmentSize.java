package ldes.client.performance;

public enum FragmentSize {

	TEN("http://localhost:10101/mobility-hindrances/pagination10?pageNumber=1");

	private final String startingEndpoint;

	FragmentSize(String startingEndpoint) {
		this.startingEndpoint = startingEndpoint;
	}

	public String getStartingEndpoint() {
		return startingEndpoint;
	}

}
