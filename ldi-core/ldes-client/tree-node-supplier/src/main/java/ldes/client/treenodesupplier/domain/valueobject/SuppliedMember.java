package ldes.client.treenodesupplier.domain.valueobject;

import org.apache.jena.rdf.model.Model;

/**
 * Wrapper around the received RDF model
 */
public class SuppliedMember {

	private final String id;
	private final Model model;

	public SuppliedMember(String id, Model model) {
        this.id = id;
        this.model = model;
	}
	public String getId() {
		return id;
	}

	public Model getModel() {
		return model;
	}
}
