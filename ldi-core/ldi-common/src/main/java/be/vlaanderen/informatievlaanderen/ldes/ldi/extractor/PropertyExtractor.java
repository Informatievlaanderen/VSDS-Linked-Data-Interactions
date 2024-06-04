package be.vlaanderen.informatievlaanderen.ldes.ldi.extractor;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import java.util.List;

@FunctionalInterface
public interface PropertyExtractor {

	List<RDFNode> getProperties(Model model);

}
