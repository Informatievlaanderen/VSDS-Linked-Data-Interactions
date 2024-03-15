package ldes.client.treenodefetcher.domain.entities;

import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;

public class TreeMember {
	private final String treeNodeUrl;
	private final String memberId;
	private final LocalDateTime createdAt;
	private final Model model;

	public TreeMember(String memberId, LocalDateTime createdAt, Model model, String treeNodeUrl) {
		this.treeNodeUrl = treeNodeUrl;
		this.memberId = memberId;
		this.createdAt = createdAt;
		this.model = model;
	}

	public String getMemberId() {
		return memberId;
	}

	public Model getModel() {
		return model;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public String getTreeNodeUrl() {
		return treeNodeUrl;
	}
}
