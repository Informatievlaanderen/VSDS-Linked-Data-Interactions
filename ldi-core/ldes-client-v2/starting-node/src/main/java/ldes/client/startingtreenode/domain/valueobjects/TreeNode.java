package ldes.client.startingtreenode.domain.valueobjects;

import java.util.Objects;

/**
 * Contains the rootNode endpoint information to start the client.
 */
public class TreeNode {
	private final String url;

	public TreeNode(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof TreeNode that))
			return false;
		return Objects.equals(url, that.url);
	}

	@Override
	public int hashCode() {
		return Objects.hash(url);
	}
}
