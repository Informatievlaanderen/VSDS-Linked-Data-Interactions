package be.vlaanderen.informatievlaanderen.ldes.ldio.util;

import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.MemberIdNotFoundException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class MemberIdExtractor {

	public static final String TREE_MEMBER = "https://w3id.org/tree#member";

	public String extractMemberId(Model model) {
		return model
				.listObjectsOfProperty(createProperty(TREE_MEMBER))
				.nextOptional()
				.map(RDFNode::toString)
				.orElseThrow(() -> new MemberIdNotFoundException(model));
	}
}
