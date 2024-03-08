package ldes.client.treenodesupplier.domain.entities;

import ldes.client.treenodesupplier.domain.valueobject.MemberStatus;
import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.sun.istack.NotNull;

public class MemberRecord implements Comparable<MemberRecord>{
	private final String memberId;

	private MemberStatus memberStatus;
	private LocalDateTime createdAt;
	private Model model;

	public MemberRecord(String memberId, Model model, LocalDateTime createdAt) {
		this(memberId, model, MemberStatus.UNPROCESSED, createdAt);
	}

	public MemberRecord(String memberId, Model model, MemberStatus memberStatus, LocalDateTime createdAt) {
		this.memberId = memberId;
		this.model = model;
		this.memberStatus = memberStatus;
		this.createdAt = createdAt;
	}

	public void processedMemberRecord() {
		this.model = null;
		this.memberStatus = MemberStatus.PROCESSED;
	}

	public String getMemberId() {
		return memberId;
	}

	public MemberStatus getMemberStatus() {
		return memberStatus;
	}

	public SuppliedMember createSuppliedMember() {
		return new SuppliedMember(memberId, model);
	}

	public Model getModel() {
		return model;
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
