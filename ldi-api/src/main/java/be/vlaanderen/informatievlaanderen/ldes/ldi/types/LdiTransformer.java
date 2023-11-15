package be.vlaanderen.informatievlaanderen.ldes.ldi.types;

import org.apache.jena.rdf.model.Model;

import java.util.Collection;
import java.util.function.Function;

/**
 * The LDI Transformer provides a user to take a linked data model (RDF) from
 * the pipeline and perform transformations
 * onto it. Afterwards, this model will be put back onto the pipeline towards a
 * next transformer or an LDI Output.
 */
public interface LdiTransformer extends LdiComponent, Function<Model, Collection<Model>> {
}