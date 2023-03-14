package ldes.client.treenodesupplier.domain.valueobject;

import org.apache.jena.rdf.model.Model;

public class SuppliedMember {

	private final Model model;

	public SuppliedMember(Model model) {
		this.model = model;
	}

	public Model getModel() {
		return model;
	}
}
