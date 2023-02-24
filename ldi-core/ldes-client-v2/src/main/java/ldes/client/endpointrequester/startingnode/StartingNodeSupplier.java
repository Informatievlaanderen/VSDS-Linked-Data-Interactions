package ldes.client.endpointrequester.startingnode;

import org.apache.jena.rdf.model.Model;

import java.util.Optional;

public interface StartingNodeSupplier {

	Optional<StartingNode> getStartingNode(Model model);

}
