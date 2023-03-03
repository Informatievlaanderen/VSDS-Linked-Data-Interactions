package be.vlaanderen.informatievlaanderen.ldes.ldi.types;

import org.apache.jena.rdf.model.Model;

import java.util.function.Function;

public interface LdiTransformer extends LdiComponent, Function<Model, Model> {
}
