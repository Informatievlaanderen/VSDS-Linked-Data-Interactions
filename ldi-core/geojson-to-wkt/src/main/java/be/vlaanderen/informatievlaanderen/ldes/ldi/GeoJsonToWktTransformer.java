package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.jena.rdf.model.*;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class GeoJsonToWktTransformer implements LdiTransformer {

    private final MyWktConverter wktConverter = new MyWktConverter();

    // TODO: 21/03/2023 support geometry
    @Override
    public Model apply(Model model) {
        wktConverter.getWktFromModel(model);
        return null;
    }

}
