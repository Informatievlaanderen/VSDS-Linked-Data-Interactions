package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;

public class Tree {
	private Tree() {
	}

	private static final String NAMESPACE = "https://w3id.org/tree#";
	/**
	 * Recommended prefix for the TREE namespace: "tree"
	 */
	public static final String PREFIX = "tree";

	public static final IRI MEMBER = IRIFactory.iriImplementation().construct(NAMESPACE + "member");
}
