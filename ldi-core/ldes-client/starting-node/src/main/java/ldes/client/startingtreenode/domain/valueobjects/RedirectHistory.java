package ldes.client.startingtreenode.domain.valueobjects;

import ldes.client.startingtreenode.exception.StartingNodeNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class RedirectHistory {
	List<String> visitedUrls;

	public RedirectHistory() {
		visitedUrls = List.of();
	}

	public RedirectHistory(List<String> visitedUrls) {
		this.visitedUrls = visitedUrls;
	}

	public RedirectHistory addStartingNodeRequest(StartingNodeRequest startingNodeRequest) {
		ArrayList<String> updatedUrls = new ArrayList<>(visitedUrls);
		if (!updatedUrls.contains(startingNodeRequest.url())) {
			updatedUrls.add(startingNodeRequest.url());
		} else {
			throw new StartingNodeNotFoundException(startingNodeRequest.url(), "Infite redirect loop.");
		}
		return new RedirectHistory(updatedUrls);
	}
}
