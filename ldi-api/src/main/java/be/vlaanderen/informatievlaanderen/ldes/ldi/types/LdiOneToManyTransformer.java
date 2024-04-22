package be.vlaanderen.informatievlaanderen.ldes.ldi.types;

import org.apache.jena.rdf.model.Model;

import java.util.List;

public interface LdiOneToManyTransformer extends LdiComponent {
	List<Model> transform(Model model);
}
