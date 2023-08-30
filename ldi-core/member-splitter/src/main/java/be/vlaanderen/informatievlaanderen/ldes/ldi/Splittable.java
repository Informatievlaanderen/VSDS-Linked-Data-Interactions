package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;

import java.util.List;

public interface Splittable {

    List<Model> split(Model model);

}
