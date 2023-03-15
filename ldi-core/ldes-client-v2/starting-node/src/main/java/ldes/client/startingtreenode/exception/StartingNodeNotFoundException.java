package ldes.client.startingtreenode.exception;


public class StartingNodeNotFoundException extends RuntimeException {
	private final String url;
	private final String comment;

	public StartingNodeNotFoundException(String url, String comment) {
		this.url = url;
		this.comment = comment;
	}

	@Override
	public String getMessage() {
		return "Starting Node could not be identified from url " + url
				+ ".\n" + comment;
	}
}
