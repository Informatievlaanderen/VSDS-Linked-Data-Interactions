package ldes.client.endpointrequester.startingnode;

import org.apache.jena.rdf.model.Model;

import java.util.Optional;

public abstract class AbstractStartingNodeSupplier implements StartingNodeSupplier {

	public static final String TREE = "https://w3id.org/tree#";

	private final StartingNodeSupplier nextHandler;

	protected AbstractStartingNodeSupplier(AbstractStartingNodeSupplier nextHandler) {
		this.nextHandler = nextHandler;
	}

	public Optional<StartingNode> getStartingNode(Model model) {
		if (nextHandler != null) {
			return nextHandler.getStartingNode(model);
		} else {
			return Optional.empty();
		}
	}

}
