package ldes.client.treenodesupplier;

public class ClientInterruptedException extends RuntimeException {

	public ClientInterruptedException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getMessage() {
		return "Client is interrupted.";
	}
}
