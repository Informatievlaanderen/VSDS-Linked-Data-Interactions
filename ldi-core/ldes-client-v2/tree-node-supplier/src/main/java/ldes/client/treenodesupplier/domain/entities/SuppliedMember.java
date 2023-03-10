package ldes.client.treenodesupplier.domain.entities;

import org.apache.jena.rdf.model.Model;

public class SuppliedMember {

	private final String memberId;
	private final Model model;

	public SuppliedMember(String memberId, Model model) {
		this.memberId = memberId;
		this.model = model;
	}

	public String getMemberId() {
		return memberId;
	}

	public Model getModel() {
		return model;
	}
}
