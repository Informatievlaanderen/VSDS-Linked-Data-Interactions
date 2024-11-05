package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.List;

public class SparqlQuery {
	private final InsertFunction insertFunction;
	private final DeleteFunction deleteFunction;

	public SparqlQuery(InsertFunction insertFunction, DeleteFunction deleteFunction) {
		this.insertFunction = insertFunction;
		this.deleteFunction = deleteFunction;
	}

	public String createQuery(Model model) {
		final List<String> subjects = model.listSubjects()
				.filterDrop(RDFNode::isAnon)
				.mapWith(RDFNode::asResource)
				.mapWith(Resource::getURI)
				.toList();

		return deleteFunction.createQueryForResources(subjects).orElse("") + "\n" + insertFunction.createQuery(model);
	}
}
