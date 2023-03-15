package ldes.client.startingtreenode.exception;

import ldes.client.startingtreenode.domain.valueobjects.StartingNodeRequest;

public class StartingNodeNotFoundException extends RuntimeException {
	private final StartingNodeRequest startingNodeRequest;

	public StartingNodeNotFoundException(StartingNodeRequest startingNodeRequest) {
		this.startingNodeRequest = startingNodeRequest;
	}

	@Override
	public String getMessage() {
		return "Starting Node could not be identified from url " + startingNodeRequest.url() + " and Content-Type "
				+ startingNodeRequest.contentType();
	}
}
