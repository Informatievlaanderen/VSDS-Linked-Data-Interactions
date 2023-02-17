package be.vlaanderen.informatievlaanderen.ldes.client.valueobjects;

import org.apache.jena.rdf.model.Model;

public class LdesMember {

	private final String memberId;
	private final Model memberModel;

	public LdesMember(final String memberId, final Model memberModel) {
		this.memberId = memberId;
		this.memberModel = memberModel;
	}

	public String getMemberId() {
		return memberId;
	}

	public Model getMemberModel() {
		return memberModel;
	}
}
