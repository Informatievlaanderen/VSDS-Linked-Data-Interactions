package ldes.client.treenodesupplier.exception;

public class NoStartingNodeException extends RuntimeException {
	private final String url;

	public NoStartingNodeException(String url) {
		this.url = url;
	}

	@Override
	public String getMessage() {
		// TODO
		return super.getMessage();
	}
}
