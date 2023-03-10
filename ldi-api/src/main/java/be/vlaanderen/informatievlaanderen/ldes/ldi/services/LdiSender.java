package be.vlaanderen.informatievlaanderen.ldes.ldi.services;

import org.apache.jena.rdf.model.Model;

import java.util.function.Consumer;

public interface LdiSender extends Consumer<Model> {
}
