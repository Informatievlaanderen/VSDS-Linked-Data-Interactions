package be.vlaanderen.informatievlaanderen.ldes.ldi.extractor;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of the PropertyExtractor
 */
public class EmptyPropertyExtractor implements PropertyExtractor {

	/**
	 * @param model ignored input
	 * @return an empty list
	 */
	@Override
	public List<RDFNode> getProperties(Model model) {
		return new ArrayList<>();
	}

}
