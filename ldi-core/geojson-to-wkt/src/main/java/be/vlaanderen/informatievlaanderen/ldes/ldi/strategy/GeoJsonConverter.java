package be.vlaanderen.informatievlaanderen.ldes.ldi.strategy;

import org.apache.jena.rdf.model.Model;

public interface GeoJsonConverter {

    boolean canHandle();

    Model handle(Model model);

}
