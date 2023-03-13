package ldes.client.treenodesupplier.domain.entities;

import ldes.client.treenodesupplier.domain.valueobject.MemberStatus;
import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import org.apache.jena.rdf.model.Model;

import java.util.Objects;

public class MemberRecord {
	private final String memberId;

	private MemberStatus memberStatus;
	private Model model;

	public MemberRecord(String memberId, Model model) {
		this(memberId, model, MemberStatus.UNPROCESSED);
	}

	public MemberRecord(String memberId, Model model, MemberStatus memberStatus) {
		this.memberId = memberId;
		this.model = model;
		this.memberStatus = memberStatus;
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
		return new SuppliedMember(model);
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
}
