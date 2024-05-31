package be.vlaanderen.informatievlaanderen.ldes.ldi.types;

import org.apache.jena.rdf.model.Model;

import java.util.List;

/**
 * The LDI Transformer provides a user to take a linked data model (RDF) from
 * the pipeline and perform transformations onto it, which can result in many linked data models.
 * Afterwards, each of these models will be put back onto the pipeline towards a next transformer or an LDI Output.
 */
@FunctionalInterface
public interface LdiOneToManyTransformer extends LdiComponent {
	List<Model> transform(Model model);
}
