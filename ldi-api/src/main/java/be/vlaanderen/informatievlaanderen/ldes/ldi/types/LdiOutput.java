package be.vlaanderen.informatievlaanderen.ldes.ldi.types;

import org.apache.jena.rdf.model.Model;

import java.util.function.Consumer;

/**
 * The LDI Output allows a user to consume the transformed linked data model
 * (RDF) from the pipeline and implement a
 * desired output approach.
 */
public interface LdiOutput extends LdiComponent, Consumer<Model> {
}
