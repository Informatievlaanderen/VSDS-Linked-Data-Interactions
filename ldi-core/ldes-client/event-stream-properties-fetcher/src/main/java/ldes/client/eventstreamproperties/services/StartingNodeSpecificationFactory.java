package ldes.client.eventstreamproperties.services;

import ldes.client.eventstreamproperties.valueobjects.StartingNodeSpecification;
import ldes.client.eventstreamproperties.valueobjects.TreeNodeSpecification;
import ldes.client.eventstreamproperties.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.Model;

public class StartingNodeSpecificationFactory {
	private StartingNodeSpecificationFactory() {
	}

	public static StartingNodeSpecification fromModel(Model model) {
		if (TreeNodeSpecification.isTreeNode(model)) {
			return new TreeNodeSpecification(model);
		}
		if (ViewSpecification.isViewSpecification(model)) {
			return new ViewSpecification(model);
		}
		throw new IllegalStateException("The provided starting node must contain either a dcterms:isPartOf property or the ldes:versionOfPath and ldes:timestampPath properties");
	}

}
