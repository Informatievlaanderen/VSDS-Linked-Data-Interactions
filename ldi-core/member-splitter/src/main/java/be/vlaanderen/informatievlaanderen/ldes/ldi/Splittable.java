package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Set;

public interface Splittable {

    Set<Model> split(Model model, String memberType);

}
