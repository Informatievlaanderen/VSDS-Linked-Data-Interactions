package be.vlaanderen.informatievlaanderen.ldes.ldi.types;

import org.apache.jena.rdf.model.Model;

import java.util.function.Consumer;

public interface LdiOutput extends LdiComponent, Consumer<Model> {
}
