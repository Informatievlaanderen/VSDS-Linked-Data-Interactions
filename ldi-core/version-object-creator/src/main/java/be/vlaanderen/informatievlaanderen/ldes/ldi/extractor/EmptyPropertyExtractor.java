package be.vlaanderen.informatievlaanderen.ldes.ldi.extractor;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import java.util.ArrayList;
import java.util.List;

public class EmptyPropertyExtractor implements PropertyExtractor {

	@Override
	public List<RDFNode> getProperties(Model model) {
		return new ArrayList<>();
	}

}
