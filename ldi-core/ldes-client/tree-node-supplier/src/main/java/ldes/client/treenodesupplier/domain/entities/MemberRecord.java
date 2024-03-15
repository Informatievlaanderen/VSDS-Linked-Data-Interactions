package ldes.client.treenodesupplier.domain.entities;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.sun.istack.NotNull;

public class MemberRecord implements Comparable<MemberRecord>{
	private final String memberId;
	private LocalDateTime createdAt;
	private Model model;
	private final String treeNodeUrl;
	private final boolean processed;

	public MemberRecord(String memberId, Model model, LocalDateTime createdAt, String treeNodeUrl, boolean processed) {
		this.memberId = memberId;
		this.model = model;
		this.createdAt = createdAt;
		this.treeNodeUrl = treeNodeUrl;
		this.processed = processed;
	}

	public String getMemberId() {
		return memberId;
	}

	public SuppliedMember createSuppliedMember() {
		return new SuppliedMember(memberId, model);
	}

	public Model getModel() {
		return model;
	}

	public String getTreeNodeUrl() {
		return treeNodeUrl;
	}

	public boolean isProcessed() {
		return processed;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof MemberRecord that))
			return false;
		return Objects.equals(memberId, that.memberId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(memberId);
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	@Override
	public int compareTo(@NotNull MemberRecord member) {
		return getCreatedAt().compareTo(member.getCreatedAt());
	}
}
