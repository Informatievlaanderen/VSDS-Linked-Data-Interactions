package be.vlaanderen.informatievlaanderen.ldes.ldi.types;

import org.apache.jena.rdf.model.Model;

/**
 * The LDI Transformer provides a user to take a linked data model (RDF) from
 * the pipeline and perform transformations
 * onto it. Afterwards, this model will be put back onto the pipeline towards a
 * next transformer or an LDI Output.
 */
@FunctionalInterface
public interface LdiOneToOneTransformer extends LdiComponent {
	Model transform(Model model);
}
