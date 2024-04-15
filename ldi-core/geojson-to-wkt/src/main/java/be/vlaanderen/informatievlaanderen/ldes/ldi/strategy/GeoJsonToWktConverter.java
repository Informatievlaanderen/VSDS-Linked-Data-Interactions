package be.vlaanderen.informatievlaanderen.ldes.ldi.strategy;

import org.apache.jena.rdf.model.Model;

public class GeoJsonToWktConverter implements GeoJsonConverter {

    @Override
    public boolean canHandle() {
        return false;
    }

    @Override
    public Model handle(Model model) {
        return null;
    }

}
