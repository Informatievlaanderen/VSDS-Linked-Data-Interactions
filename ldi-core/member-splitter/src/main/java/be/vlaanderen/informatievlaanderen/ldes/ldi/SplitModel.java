package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class SplitModel {

	public Set<Model> split(Model inputModel, String memberType) {
		return inputModel
				.listSubjectsWithProperty(RDF.type, createProperty(memberType))
				.mapWith(subject -> extractModelForSubject(inputModel, subject))
				.toSet();
	}

	private Model extractModelForSubject(Model inputModel, Resource subject) {
		final Deque<Resource> subjectsOfIncludedStatements = new ArrayDeque<>();
		subjectsOfIncludedStatements.push(subject);
		final Model memberModel = ModelFactory.createDefaultModel();
		while (isNotEmpty(subjectsOfIncludedStatements)) {
			final Resource includedSubject = subjectsOfIncludedStatements.pop();
			inputModel.listStatements(includedSubject, null, (String) null)
					.forEach(includedStatement -> {
						memberModel.add(includedStatement);
						RDFNode object = includedStatement.getObject();
						if (object.isResource()) {
							subjectsOfIncludedStatements.push(object.asResource());
						}
					});
		}
		return memberModel;
	}
}
